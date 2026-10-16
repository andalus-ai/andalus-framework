package tech.kayys.andalus.a2a.durable;

import tech.kayys.andalus.a2a.model.A2AArtifact;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Application-scoped registry of durable A2A tasks (Phase 8 §40-42).
 *
 * <p>Responsibilities (6.16.5.22 — narrowed for Claw):
 * <ul>
 *   <li>Create and track {@link DurableA2ATask} instances keyed by taskId.</li>
 *   <li>Own A2A task persistence/correlation: task identity, callerExecutionId,
 *       remoteExecutionId, remote status, checkpoint correlation, event sequence,
 *       retry count, result/artifact metadata.</li>
 *   <li>Provide status-update entry points called by the transport layer
 *       ({@link #update}, {@link #complete}, {@link #fail}, {@link #cancel}).</li>
 * </ul>
 *
 * <p>The legacy {@link RemoteAgentWaitState} support is retained for backward
 * compatibility (6.16.23) but is <em>not</em> the Claw waiting mechanism — durable
 * WAITING lifecycle belongs to the {@code DurableWaitController}, reached through
 * {@code A2ACompletionBridge} / {@code A2AWakeCoordinator}. This ledger must not
 * become a second execution scheduler.</p>
 *
 * <p>This class does NOT depend on CDI so it can be used standalone or in tests.
 * In a CDI deployment, wrap it with {@code @ApplicationScoped} in a producer.</p>
 */
public class DurableA2ATaskLedger {

    private static final Logger LOG = Logger.getLogger(DurableA2ATaskLedger.class.getName());

    /** taskId → task */
    private final Map<String, DurableA2ATask> tasks = new ConcurrentHashMap<>();

    /** taskId → wait state (only present while task is in-flight) */
    private final Map<String, RemoteAgentWaitState> waitStates = new ConcurrentHashMap<>();

    // ── Task lifecycle ────────────────────────────────────────────────────────

    /**
     * Creates a new durable task and registers it.
     *
     * @param callerExecutionId  local execution ID that initiated the call
     * @param remoteEndpoint     URI of the remote agent
     * @param checkpointId       checkpoint saved before the call (resume anchor)
     * @return the newly created task
     */
    public DurableA2ATask create(String callerExecutionId, String remoteEndpoint, String checkpointId) {
        String taskId = UUID.randomUUID().toString();
        DurableA2ATask task = DurableA2ATask.create(taskId, callerExecutionId, remoteEndpoint, checkpointId);
        tasks.put(taskId, task);
        LOG.info(() -> "DurableA2A task created: " + taskId + " for exec=" + callerExecutionId);
        return task;
    }

    /**
     * Creates a task using the canonical Claw identity model (6.16.3): the remote
     * execution id stays unassigned until {@link #bindRemoteExecution(String, String)}
     * is called with the identity discovered from the remote agent.
     */
    public DurableA2ATask createForCaller(String callerExecutionId, String remoteEndpoint, String checkpointId) {
        String taskId = UUID.randomUUID().toString();
        DurableA2ATask task = DurableA2ATask.createForCaller(taskId, callerExecutionId, remoteEndpoint, checkpointId);
        tasks.put(taskId, task);
        LOG.info(() -> "DurableA2A task created: " + taskId + " for exec=" + callerExecutionId);
        return task;
    }

    /**
     * Creates a task from a durable invocation intent (6.16.2): the intent's stable
     * {@code taskId} is used instead of a random one, and the invocation identity
     * (execution id, operation id, idempotency key) is propagated into the task
     * metadata so it survives restart and drives reconciliation (6.16.2.16).
     *
     * <p>Idempotent: if a task with the intent's id already exists, the existing
     * task is returned — replay with the same logical operation never creates a
     * second task (6.16.1.5, invariant 5).</p>
     */
    public DurableA2ATask createFromIntent(A2AInvocationIntent intent, String checkpointId) {
        java.util.Objects.requireNonNull(intent, "intent");
        return tasks.compute(intent.taskId(), (id, existing) -> {
            if (existing != null) {
                return existing;
            }
            DurableA2ATask task = DurableA2ATask.createForCaller(
                    intent.taskId(), intent.executionId(), intent.remoteEndpoint(), checkpointId)
                    .withMetadata(intent.toMetadata());
            LOG.info(() -> "DurableA2A task created from intent: " + intent.taskId()
                    + " op=" + intent.operationId() + " for exec=" + intent.executionId());
            return task;
        });
    }

    // ── Remote execution binding (6.16.3) ─────────────────────────────────────

    /**
     * Binds a task to the remote execution id reported by the remote agent.
     *
     * <p>Idempotent: re-binding the same remote execution id is a no-op.
     * Binding a <em>different</em> remote execution id is a correlation conflict.</p>
     *
     * @throws IllegalArgumentException if the task is unknown
     * @throws IllegalStateException    if the task is already bound to another remote execution
     */
    public DurableA2ATask bindRemoteExecution(String taskId, String remoteExecutionId) {
        java.util.Objects.requireNonNull(taskId, "taskId");
        java.util.Objects.requireNonNull(remoteExecutionId, "remoteExecutionId");

        DurableA2ATask updated = tasks.compute(taskId, (id, existing) -> {
            if (existing == null) {
                return null;
            }
            if (existing.executionId() != null
                    && !existing.executionId().equals(remoteExecutionId)) {
                throw new IllegalStateException(
                        "A2A task " + taskId + " is already bound to remote execution "
                                + existing.executionId());
            }
            return existing.withRemoteExecutionId(remoteExecutionId);
        });

        if (updated == null) {
            throw new IllegalArgumentException("Unknown A2A task: " + taskId);
        }
        return updated;
    }

    /**
     * Creates a wait state for the given task and returns it.
     * The caller blocks on {@link RemoteAgentWaitState#await()} until the remote resolves.
     */
    public RemoteAgentWaitState createWaitState(String taskId, Duration timeout) {
        DurableA2ATask task = tasks.get(taskId);
        if (task == null) throw new IllegalArgumentException("Unknown taskId: " + taskId);

        RemoteAgentWaitState ws = RemoteAgentWaitState.of(taskId, task.callerExecutionId(), timeout);
        waitStates.put(taskId, ws);
        LOG.fine(() -> "Wait state created for task " + taskId);
        return ws;
    }

    /** Convenience overload with default 10-minute timeout. */
    public RemoteAgentWaitState createWaitState(String taskId) {
        return createWaitState(taskId, Duration.ofMinutes(10));
    }

    // ── Status updates (called by transport / polling) ────────────────────────

    /** Transitions a task to a new status. Fires resolution if terminal. */
    public DurableA2ATask update(String taskId, A2ATaskStatus newStatus) {
        DurableA2ATask updated = tasks.compute(taskId, (k, existing) -> {
            if (existing == null) {
                LOG.warning("update called for unknown task: " + taskId);
                return null;
            }
            return existing.withStatus(newStatus);
        });

        if (updated == null) return null;

        LOG.fine(() -> "Task " + taskId + " → " + newStatus);

        if (updated.isTerminal()) {
            resolveWaitState(taskId, updated);
        }

        return updated;
    }

    /** Marks a task COMPLETED and fires the wait state. */
    public DurableA2ATask complete(String taskId) {
        return update(taskId, A2ATaskStatus.COMPLETED);
    }

    /**
     * Marks a task COMPLETED, persisting the protocol-visible result
     * (artifacts + metadata) <em>before</em> the terminal transition (6.16.5.14).
     *
     * <p>This is the durable handoff point: once this method returns, crash recovery
     * can always reconstruct the outcome from the ledger.</p>
     */
    public DurableA2ATask complete(String taskId, List<A2AArtifact> artifacts, Map<String, Object> metadata) {
        DurableA2ATask updated = tasks.compute(taskId, (k, existing) -> {
            if (existing == null) {
                return null;
            }
            DurableA2ATask t = existing;
            if (artifacts != null && !artifacts.isEmpty()) {
                t = t.withArtifacts(artifacts);
            }
            if (metadata != null && !metadata.isEmpty()) {
                Map<String, Object> merged = new java.util.HashMap<>(t.metadata());
                merged.putAll(metadata);
                t = t.withMetadata(merged);
            }
            return t.withStatus(A2ATaskStatus.COMPLETED);
        });

        if (updated == null) return null;

        LOG.fine(() -> "Task " + taskId + " → COMPLETED (result persisted)");
        resolveWaitState(taskId, updated);
        return updated;
    }

    /** Marks a task FAILED and fires the wait state with a reason. */
    public DurableA2ATask fail(String taskId, String reason) {
        DurableA2ATask updated = update(taskId, A2ATaskStatus.FAILED);
        RemoteAgentWaitState ws = waitStates.get(taskId);
        if (ws != null && updated != null) {
            ws.fail(updated, reason);
            waitStates.remove(taskId);
        }
        return updated;
    }

    /** Cancels a task and its wait state. */
    public DurableA2ATask cancel(String taskId) {
        DurableA2ATask updated = update(taskId, A2ATaskStatus.CANCELLED);
        RemoteAgentWaitState ws = waitStates.remove(taskId);
        if (ws != null) ws.cancel();
        return updated;
    }

    /** Increments the retry counter and resets to PENDING. */
    public DurableA2ATask retry(String taskId) {
        return tasks.compute(taskId, (k, existing) ->
            existing == null ? null
                : existing.incrementRetry().withStatus(A2ATaskStatus.PENDING));
    }

    // ── Checkpoint / resume (§42) ─────────────────────────────────────────────

    /** Updates the checkpoint anchor for a task. */
    public void updateCheckpoint(String taskId, String checkpointId) {
        tasks.computeIfPresent(taskId, (k, t) -> t.withCheckpointId(checkpointId));
    }

    /** Updates the last ledger sequence number for distributed observability. */
    public void updateEventSeq(String taskId, long seq) {
        tasks.computeIfPresent(taskId, (k, t) -> t.withLastEventSeq(seq));
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public Optional<DurableA2ATask> find(String taskId) {
        return Optional.ofNullable(tasks.get(taskId));
    }

    public List<DurableA2ATask> allPending() {
        List<DurableA2ATask> result = new ArrayList<>();
        tasks.values().stream().filter(DurableA2ATask::isPending).forEach(result::add);
        return result;
    }

    public List<DurableA2ATask> byCallerExecution(String callerExecutionId) {
        List<DurableA2ATask> result = new ArrayList<>();
        tasks.values().stream()
            .filter(t -> callerExecutionId.equals(t.callerExecutionId()))
            .forEach(result::add);
        return result;
    }

    /**
     * Finds a task only when it belongs to the given caller execution (6.16.3).
     *
     * <p>Prevents one execution from observing — or being woken by — another
     * execution's A2A tasks.</p>
     */
    public Optional<DurableA2ATask> findForCallerExecution(String callerExecutionId, String taskId) {
        if (callerExecutionId == null || taskId == null) {
            return Optional.empty();
        }
        DurableA2ATask task = tasks.get(taskId);
        if (task == null || !callerExecutionId.equals(task.callerExecutionId())) {
            return Optional.empty();
        }
        return Optional.of(task);
    }

    /**
     * All tracked tasks, in no particular order — used by startup recovery, which
     * must consider terminal-but-unwoken tasks as well as pending ones (6.16.5.21).
     */
    public List<DurableA2ATask> allTasks() {
        return List.copyOf(tasks.values());
    }

    public int taskCount()     { return tasks.size(); }
    public int waitingCount()  { return waitStates.size(); }

    // ── Internal ──────────────────────────────────────────────────────────────

    private void resolveWaitState(String taskId, DurableA2ATask task) {
        RemoteAgentWaitState ws = waitStates.remove(taskId);
        if (ws == null) return;
        switch (task.status()) {
            case COMPLETED -> ws.complete(task);
            case FAILED    -> ws.fail(task, "Remote agent reported FAILED");
            case CANCELLED -> ws.cancel();
            default        -> {} // no-op for non-terminal
        }
    }
}
