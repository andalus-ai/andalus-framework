package tech.kayys.andalus.a2a.durable;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Narrow recovery/reconciliation boundary for durable A2A operations (6.16.6.29).
 *
 * <p>The implementation composes — but does not own — the {@link DurableA2ATaskLedger},
 * the {@link A2ACompletionBridge}, and the remote {@code A2AClient}. Execution state,
 * checkpoints, journal, retry policy, and compensation remain Claw subsystems.</p>
 *
 * <p>All operations are idempotent and non-blocking: recovery workers may run
 * concurrently and may repeat reconciliation after a crash (6.16.6.9, 6.16.6.19).</p>
 */
public interface A2AReconciliationService {

    /**
     * Reconciles all non-terminal A2A tasks belonging to the given local execution.
     * Used during startup/WAITING recovery of an execution (6.16.6.17).
     *
     * @param callerExecutionId local Claw execution id
     * @return the decision reached for each reconciled task
     */
    CompletionStage<List<A2ARecoveryDecision>> reconcile(String callerExecutionId);

    /**
     * Reconciles one A2A task with the remote agent.
     *
     * @param taskId A2A protocol task id
     * @return the known remote state of the task
     */
    CompletionStage<A2ARecoveryDecision> reconcileTask(String taskId);

    /**
     * Best-effort, non-blocking remote cancellation of all non-terminal A2A tasks
     * belonging to a locally-cancelled execution (6.16.7).
     *
     * <p>Local cancellation is authoritative: the remote cancellation result never
     * changes the local execution state. Failures leave the task for later
     * reconciliation (6.16.7.8). Cancellation by {@code taskId} is idempotent —
     * "already cancelled / completed / not found" are reconciliation outcomes,
     * not new failures (6.16.7.19).</p>
     *
     * @param callerExecutionId local Claw execution id
     * @return the number of remote cancellation attempts issued
     */
    CompletionStage<Integer> cancelPendingTasks(String callerExecutionId);
}
