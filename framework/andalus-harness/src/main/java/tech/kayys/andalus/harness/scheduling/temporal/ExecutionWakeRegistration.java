package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages transient wait-lists for executions waiting on another execution's completion (6.15.5.12).
 *
 * <p>Supports one-to-many dependency: multiple executions may wait on one target execution (6.15.5.14).
 * Enforces that an execution cannot wait for itself (6.15.5.15).</p>
 */
public final class ExecutionWakeRegistration implements ExecutionCompletionListener {

    private final Map<ExecutionId, Map<ExecutionId, ExecutionId>> registrations =
            new ConcurrentHashMap<>();

    private final DurableWaitController waitController;

    public ExecutionWakeRegistration(DurableWaitController waitController) {
        this.waitController = Objects.requireNonNull(waitController, "waitController");
    }

    /**
     * Registers a waiting execution against a target execution.
     *
     * @param waitingExecutionId the execution waiting to resume
     * @param targetExecutionId  the execution whose completion should wake it
     * @throws IllegalArgumentException if waitingExecutionId equals targetExecutionId (6.15.5.15)
     */
    public void register(ExecutionId waitingExecutionId, ExecutionId targetExecutionId) {
        Objects.requireNonNull(waitingExecutionId, "waitingExecutionId");
        Objects.requireNonNull(targetExecutionId, "targetExecutionId");

        if (waitingExecutionId.equals(targetExecutionId)) {
            throw new IllegalArgumentException(
                    "Execution cannot wait for itself: " + waitingExecutionId
            );
        }

        registrations
                .computeIfAbsent(targetExecutionId, ignored -> new ConcurrentHashMap<>())
                .put(waitingExecutionId, waitingExecutionId);
    }

    @Override
    public void completed(ExecutionId targetExecutionId) {
        targetCompleted(targetExecutionId);
    }

    /**
     * Notifies all waiting executions that the target execution has completed.
     *
     * @param targetExecutionId the target execution that reached terminal status
     */
    public void targetCompleted(ExecutionId targetExecutionId) {
        Map<ExecutionId, ExecutionId> waiters = registrations.remove(targetExecutionId);
        if (waiters == null) {
            return;
        }

        for (ExecutionId waitingExecutionId : waiters.keySet()) {
            waitController.wake(waitingExecutionId);
        }
    }

    /**
     * Cancels a wait dependency between a waiting execution and target execution.
     *
     * @param waitingExecutionId the waiting execution
     * @param targetExecutionId  the target execution
     */
    public void cancel(ExecutionId waitingExecutionId, ExecutionId targetExecutionId) {
        if (targetExecutionId == null || waitingExecutionId == null) {
            return;
        }
        Map<ExecutionId, ExecutionId> waiters = registrations.get(targetExecutionId);
        if (waiters == null) {
            return;
        }

        waiters.remove(waitingExecutionId);
        if (waiters.isEmpty()) {
            registrations.remove(targetExecutionId, waiters);
        }
    }

    /**
     * Cancels all registrations where the given execution is the waiter.
     */
    public void cancelAll(ExecutionId waitingExecutionId) {
        if (waitingExecutionId == null) return;
        for (var entry : registrations.entrySet()) {
            entry.getValue().remove(waitingExecutionId);
            if (entry.getValue().isEmpty()) {
                registrations.remove(entry.getKey(), entry.getValue());
            }
        }
    }

    /** Returns count of target executions currently being observed (for tests). */
    int activeTargetCount() {
        return registrations.size();
    }
}
