package tech.kayys.andalus.a2a.durable;

import tech.kayys.andalus.a2a.model.A2AArtifact;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Default implementation of {@link DurableA2AOperationService} (6.16).
 *
 * <p>Wires together the existing {@link DurableA2ATaskLedger} for remote task tracking,
 * and a {@link WakeCallback} functional interface so the harness integration adapter
 * can inject the actual {@code DurableWaitCoordinator} without creating a compile-time
 * dependency from {@code andalus-a2a} to {@code andalus-harness} (SOLID / separation of concerns).
 *
 * <p>Storage is currently in-memory. Production deployments replace this with a durable
 * backing store. The interface contract is idempotent, so the store can be swapped freely.
 */
public class DefaultDurableA2AOperationService implements DurableA2AOperationService {

    private static final Logger LOG = Logger.getLogger(DefaultDurableA2AOperationService.class.getName());

    /**
     * Callback that parks a caller execution into WAITING state (bridges to harness layer).
     * Receives the String callerExecutionId and the A2A task ID used as the wake key.
     */
    @FunctionalInterface
    public interface WakeCallback {
        /** Wake the given execution (identified by its String ID). */
        void wakeExecution(String callerExecutionId);
    }

    /**
     * Callback that enters a WAITING state for the given execution.
     * Receives callerExecutionId and an optional wake-condition hint (the A2A task ID).
     */
    @FunctionalInterface
    public interface EnterWaitCallback {
        void enterWaiting(String callerExecutionId, String wakeHint);
    }

    private final DurableA2ATaskLedger ledger;
    private final EnterWaitCallback    enterWaitCallback;
    private final WakeCallback         wakeCallback;

    /** operationId → operation record */
    private final Map<String, A2AOperationRecord> operations     = new ConcurrentHashMap<>();

    /** idempotencyKey → operationId (de-duplicate retries — 6.16.13) */
    private final Map<String, String>             idempotencyIdx = new ConcurrentHashMap<>();

    public DefaultDurableA2AOperationService(DurableA2ATaskLedger ledger,
                                              EnterWaitCallback    enterWaitCallback,
                                              WakeCallback         wakeCallback) {
        this.ledger            = Objects.requireNonNull(ledger,            "ledger");
        this.enterWaitCallback = Objects.requireNonNull(enterWaitCallback, "enterWaitCallback");
        this.wakeCallback      = Objects.requireNonNull(wakeCallback,      "wakeCallback");
    }

    // ── beginOperation ───────────────────────────────────────────────────────

    @Override
    public A2AOperationRecord beginOperation(String callerExecutionId, String remoteEndpoint,
                                              String idempotencyKey,    String checkpointId) {
        Objects.requireNonNull(callerExecutionId, "callerExecutionId");
        Objects.requireNonNull(remoteEndpoint,    "remoteEndpoint");

        // Idempotency guard: return existing non-terminal op for the same key (6.16.13)
        if (idempotencyKey != null) {
            String existingOpId = idempotencyIdx.get(idempotencyKey);
            if (existingOpId != null) {
                A2AOperationRecord existing = operations.get(existingOpId);
                if (existing != null && !existing.isTerminal()) {
                    LOG.fine(() -> "Idempotency hit key=" + idempotencyKey + " → reuse op=" + existingOpId);
                    return existing;
                }
            }
        }

        A2AOperationRecord op = idempotencyKey != null
                ? A2AOperationRecord.createWithKey(callerExecutionId, remoteEndpoint, idempotencyKey, checkpointId)
                : A2AOperationRecord.create(callerExecutionId, remoteEndpoint, checkpointId);

        persist(op);
        LOG.info(() -> "A2A operation created: " + op.operationId() + " for exec=" + callerExecutionId);
        return op;
    }

    // ── submitToRemote ───────────────────────────────────────────────────────

    @Override
    public A2AOperationRecord submitToRemote(String operationId, String a2aTaskId) {
        A2AOperationRecord op = require(operationId);
        A2AOperationRecord submitted = op.withStatus(A2AOperationStatus.SUBMITTED);
        if (a2aTaskId != null) {
            submitted = submitted.withA2aTaskId(a2aTaskId);
        }
        persist(submitted);
        LOG.fine(() -> "A2A op=" + operationId + " submitted → taskId=" + a2aTaskId);
        return submitted;
    }

    // ── enterWaiting ─────────────────────────────────────────────────────────

    @Override
    public A2AOperationRecord enterWaiting(String operationId) {
        A2AOperationRecord op = require(operationId);

        // Wake hint: the A2A task ID is the natural wake correlation key
        String wakeHint = op.taskId().orElse("op:" + operationId);

        // Delegate to harness layer via callback (thread-free WAITING — 6.16.5)
        enterWaitCallback.enterWaiting(op.callerExecutionId(), wakeHint);

        A2AOperationRecord waiting = op.withStatus(A2AOperationStatus.WAITING_FOR_REMOTE);
        persist(waiting);
        LOG.info(() -> "exec=" + op.callerExecutionId() + " WAITING for A2A op=" + operationId);
        return waiting;
    }

    // ── completeOperation ────────────────────────────────────────────────────

    @Override
    public A2AOperationRecord completeOperation(String operationId, List<A2AArtifact> artifacts) {
        A2AOperationRecord op = require(operationId);

        // 1. Persist result FIRST — result is durable before wake (6.16.21)
        A2AOperationRecord completed = op.withStatus(A2AOperationStatus.COMPLETED);
        persist(completed);
        LOG.info(() -> "A2A op=" + operationId + " COMPLETED; waking exec=" + op.callerExecutionId());

        // 2. Update ledger task if known
        op.taskId().ifPresent(taskId -> ledger.complete(taskId));

        // 3. Wake caller AFTER result is durable
        wakeCallback.wakeExecution(op.callerExecutionId());

        return completed;
    }

    // ── failOperation ────────────────────────────────────────────────────────

    @Override
    public A2AOperationRecord failOperation(String operationId, String reason) {
        A2AOperationRecord op = require(operationId);
        A2AOperationRecord failed = op.withStatus(A2AOperationStatus.FAILED);
        persist(failed);
        LOG.warning(() -> "A2A op=" + operationId + " FAILED: " + reason);

        op.taskId().ifPresent(taskId -> ledger.fail(taskId, reason));
        wakeCallback.wakeExecution(op.callerExecutionId());

        return failed;
    }

    // ── cancelOperation ──────────────────────────────────────────────────────

    @Override
    public A2AOperationRecord cancelOperation(String operationId) {
        A2AOperationRecord op = require(operationId);
        A2AOperationRecord cancelled = op.withStatus(A2AOperationStatus.CANCELLED);
        persist(cancelled);

        op.taskId().ifPresent(taskId -> ledger.cancel(taskId));
        wakeCallback.wakeExecution(op.callerExecutionId());

        return cancelled;
    }

    // ── reconcile ────────────────────────────────────────────────────────────

    @Override
    public A2AOperationRecord reconcile(String operationId) {
        A2AOperationRecord op = require(operationId);

        // No task ID assigned → operation never reached remote; reset for retry
        Optional<String> taskIdOpt = op.taskId();
        if (taskIdOpt.isEmpty()) {
            A2AOperationRecord pending = op.withStatus(A2AOperationStatus.PENDING);
            persist(pending);
            LOG.info(() -> "A2A op=" + operationId + " reconcile: no taskId → PENDING for retry");
            return pending;
        }

        String taskId = taskIdOpt.get();
        Optional<DurableA2ATask> remoteTask = ledger.find(taskId);

        if (remoteTask.isEmpty()) {
            A2AOperationRecord unknown = op.withStatus(A2AOperationStatus.UNKNOWN);
            persist(unknown);
            LOG.warning(() -> "A2A op=" + operationId + " reconcile: task=" + taskId + " not in ledger → UNKNOWN");
            return unknown;
        }

        DurableA2ATask task = remoteTask.get();
        return switch (task.status()) {
            case COMPLETED -> {
                LOG.info(() -> "A2A op=" + operationId + " reconcile: remote COMPLETED → complete locally");
                yield completeOperation(operationId, task.artifacts());
            }
            case FAILED -> {
                LOG.info(() -> "A2A op=" + operationId + " reconcile: remote FAILED → fail locally");
                yield failOperation(operationId, "Remote task failed during reconciliation");
            }
            case CANCELLED -> {
                LOG.info(() -> "A2A op=" + operationId + " reconcile: remote CANCELLED → cancel locally");
                yield cancelOperation(operationId);
            }
            default -> {
                // Still running — re-register WAITING; no new remote request (6.16.26)
                LOG.info(() -> "A2A op=" + operationId + " reconcile: remote " + task.status() + " → re-register WAITING");
                yield enterWaiting(operationId);
            }
        };
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    @Override
    public Optional<A2AOperationRecord> find(String operationId) {
        return Optional.ofNullable(operations.get(operationId));
    }

    @Override
    public List<A2AOperationRecord> findByCallerExecution(String callerExecutionId) {
        List<A2AOperationRecord> result = new ArrayList<>();
        operations.values().stream()
                .filter(op -> callerExecutionId.equals(op.callerExecutionId()))
                .forEach(result::add);
        return result;
    }

    @Override
    public Optional<A2AOperationRecord> findByIdempotencyKey(String idempotencyKey) {
        return Optional.ofNullable(idempotencyIdx.get(idempotencyKey))
                .map(operations::get);
    }

    // ── Internal ─────────────────────────────────────────────────────────────

    private void persist(A2AOperationRecord op) {
        operations.put(op.operationId(), op);
        idempotencyIdx.put(op.idempotencyKey(), op.operationId());
    }

    private A2AOperationRecord require(String operationId) {
        A2AOperationRecord op = operations.get(operationId);
        if (op == null) throw new IllegalArgumentException("Unknown A2A operation: " + operationId);
        return op;
    }
}
