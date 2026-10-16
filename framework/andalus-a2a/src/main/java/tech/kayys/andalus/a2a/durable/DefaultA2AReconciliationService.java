package tech.kayys.andalus.a2a.durable;

import tech.kayys.andalus.a2a.api.A2AClient;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

/**
 * Default {@link A2AReconciliationService} composing the durable ledger, the
 * completion bridge, an {@link A2AReconciler}, and the {@link A2AClient} (6.16.6.29).
 *
 * <p>Decision handling (6.16.6.8 / 6.16.6.30): {@code FOUND_*} terminal states are
 * persisted through the {@link A2ACompletionBridge} (completion-before-wake,
 * duplicate safe); {@code FOUND_RUNNING} only re-affirms the RUNNING marker;
 * {@code NOT_FOUND} and {@code UNKNOWN} never mutate local state and never trigger
 * blind resubmission — idempotency/submission policy belongs to Claw (6.16.6.12).</p>
 */
public final class DefaultA2AReconciliationService implements A2AReconciliationService {

    private static final Logger LOG = Logger.getLogger(DefaultA2AReconciliationService.class.getName());

    /** Metadata keys carrying the durable invocation identity (6.16.2.16). */
    public static final String META_EXECUTION_ID = A2AInvocationIntent.META_EXECUTION_ID;
    public static final String META_OPERATION_ID = A2AInvocationIntent.META_OPERATION_ID;
    public static final String META_A2A_TASK_ID = A2AInvocationIntent.META_A2A_TASK_ID;
    public static final String META_IDEMPOTENCY_KEY = A2AInvocationIntent.META_IDEMPOTENCY_KEY;

    /** Legacy keys emitted by earlier Wayang-era integration — still honoured. */
    private static final String LEGACY_OPERATION_ID = "wayang.operation.id";
    private static final String LEGACY_IDEMPOTENCY_KEY = "wayang.idempotency.key";

    private final DurableA2ATaskLedger ledger;
    private final A2ACompletionBridge completionBridge;
    private final A2AReconciler reconciler;
    private final A2AClient client;

    public DefaultA2AReconciliationService(
            DurableA2ATaskLedger ledger,
            A2ACompletionBridge completionBridge,
            A2AReconciler reconciler,
            A2AClient client) {
        this.ledger = Objects.requireNonNull(ledger, "ledger");
        this.completionBridge = Objects.requireNonNull(completionBridge, "completionBridge");
        this.reconciler = Objects.requireNonNull(reconciler, "reconciler");
        this.client = Objects.requireNonNull(client, "client");
    }


    // ── Reconciliation ────────────────────────────────────────────────────────

    @Override
    public CompletionStage<List<A2ARecoveryDecision>> reconcile(String callerExecutionId) {
        Objects.requireNonNull(callerExecutionId, "callerExecutionId");

        List<DurableA2ATask> pending = ledger.byCallerExecution(callerExecutionId).stream()
                .filter(t -> !t.isTerminal())
                .toList();

        if (pending.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }

        List<CompletableFuture<A2ARecoveryDecision>> futures = pending.stream()
                .map(t -> reconcileTask(t.taskId()).toCompletableFuture())
                .toList();

        return CompletableFuture
                .allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(v -> {
                    List<A2ARecoveryDecision> decisions = new ArrayList<>(futures.size());
                    futures.forEach(f -> decisions.add(f.join()));
                    return List.copyOf(decisions);
                });
    }

    @Override
    public CompletionStage<A2ARecoveryDecision> reconcileTask(String taskId) {
        Objects.requireNonNull(taskId, "taskId");

        DurableA2ATask task = ledger.find(taskId).orElse(null);
        if (task == null) {
            LOG.warning("Reconciliation requested for unknown A2A task: " + taskId);
            return CompletableFuture.completedFuture(A2ARecoveryDecision.NOT_FOUND);
        }

        return reconciler.reconcile(intentFrom(task), task)
                .thenApply(decision -> {
                    applyDecision(task, decision);
                    return decision;
                });
    }

    // ── Cancellation propagation (6.16.7) ─────────────────────────────────────

    @Override
    public CompletionStage<Integer> cancelPendingTasks(String callerExecutionId) {
        Objects.requireNonNull(callerExecutionId, "callerExecutionId");

        List<DurableA2ATask> pending = ledger.byCallerExecution(callerExecutionId).stream()
                .filter(t -> !t.isTerminal())
                .toList();

        for (DurableA2ATask task : pending) {
            // Non-blocking, idempotent remote cancellation (6.16.7.6 / 6.16.7.19).
            client.cancelTask(task.taskId()).whenComplete((ok, error) -> {
                if (error == null) {
                    // Remote accepted cancellation — persist + wake. If the local
                    // execution is already CANCELED, the wake validates durable
                    // state and is a no-op (6.16.7.9).
                    completionBridge.cancelled(task.taskId());
                } else {
                    // Remote outcome is UNKNOWN; local CANCELED stands and recovery
                    // reconciliation will settle the task later (6.16.7.8).
                    LOG.warning("Remote cancellation of A2A task " + task.taskId()
                            + " inconclusive: " + error.getMessage());
                }
            });
        }

        return CompletableFuture.completedFuture(pending.size());
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private void applyDecision(DurableA2ATask task, A2ARecoveryDecision decision) {
        switch (decision) {
            case FOUND_RUNNING -> {
                if (!task.isTerminal() && task.status() != A2ATaskStatus.RUNNING) {
                    ledger.update(task.taskId(), A2ATaskStatus.RUNNING);
                }
            }
            case FOUND_COMPLETED -> completionBridge.completed(task.taskId());
            case FOUND_FAILED ->
                completionBridge.failed(task.taskId(), "Remote agent reported FAILED during reconciliation");
            case FOUND_CANCELLED -> completionBridge.cancelled(task.taskId());
            case NOT_FOUND ->
                // Never a blind resubmission — submission policy decides (6.16.6.12).
                LOG.info("A2A task " + task.taskId()
                        + " not found remotely; awaiting idempotency/submission policy");
            case UNKNOWN ->
                // Remain unresolved; reconciliation will be retried (6.16.6.30).
                LOG.fine(() -> "A2A task " + task.taskId() + " remote state UNKNOWN — will retry");
        }
    }

    /**
     * Reconstructs the invocation intent from the durable task and its propagated
     * metadata. Current {@code andalus.*} keys are preferred; legacy
     * {@code wayang.*} keys are honoured for pre-rename tasks.
     */
    private static A2AInvocationIntent intentFrom(DurableA2ATask task) {
        String operationId = metadata(task, META_OPERATION_ID, LEGACY_OPERATION_ID);
        if (operationId == null) {
            operationId = "op-a2a-" + task.taskId();
        }
        String idempotencyKey = metadata(task, META_IDEMPOTENCY_KEY, LEGACY_IDEMPOTENCY_KEY);

        return idempotencyKey != null
                ? A2AInvocationIntent.keyed(
                        task.callerExecutionId(), operationId, task.taskId(), task.remoteEndpoint(), idempotencyKey)
                : A2AInvocationIntent.withoutKey(
                        task.callerExecutionId(), operationId, task.taskId(), task.remoteEndpoint());
    }

    private static String metadata(DurableA2ATask task, String key, String legacyKey) {
        Object value = task.metadata().get(key);
        if (value == null) {
            value = task.metadata().get(legacyKey);
        }
        return value instanceof String s && !s.isBlank() ? s : null;
    }
}
