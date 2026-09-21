package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

/**
 * Coordinates the durable WAITING lifecycle for an execution (6.15).
 *
 * <p>WAITING is a first-class durable parked state — not a sleeping thread. When an execution
 * enters WAITING, the worker is released and the wake condition + deadline are persisted durably.
 * The execution can survive process crashes, deployments, and long periods of inactivity while
 * retaining its identity, cursor, context, wake condition, and deadline.
 *
 * <p>Implementations must ensure:
 * <ul>
 *   <li>Entering WAITING checkpoints state and appends {@code EXECUTION_WAITING} to the journal
 *       before releasing the worker</li>
 *   <li>Waking an execution verifies it is still WAITING (not terminal, not expired)</li>
 *   <li>Expired executions (deadline exceeded at wake time) transition to TIMEOUT instead of RUNNING</li>
 *   <li>All operations are idempotent — a crash during enter/wake can be safely retried</li>
 * </ul>
 */
public interface DurableWaitCoordinator {

    /**
     * Transitions a RUNNING execution to WAITING with the given wake condition and optional deadline.
     *
     * <p>Sequence:
     * <ol>
     *   <li>Validate deadline is not already exceeded</li>
     *   <li>Persist WAITING state (checkpoint + store)</li>
     *   <li>Append {@code EXECUTION_WAITING} journal event</li>
     *   <li>Register wake intent (timer for TimeWake, event subscription otherwise)</li>
     * </ol>
     *
     * @param executionId   the execution to park
     * @param wakeCondition what will wake this execution
     * @param deadline      optional maximum wait time; if exceeded at wake time, execution → TIMEOUT
     * @throws IllegalStateException if the execution is not in a state that can transition to WAITING
     */
    void enterWaiting(ExecutionId executionId, WakeCondition wakeCondition, java.util.Optional<Deadline> deadline);

    /**
     * Wakes a WAITING execution, transitioning it toward RUNNING.
     *
     * <p>Sequence:
     * <ol>
     *   <li>Reload durable state</li>
     *   <li>If terminal or not WAITING → ignore (idempotent)</li>
     *   <li>If deadline exceeded → transition to TIMEOUT</li>
     *   <li>Otherwise → transition WAITING → RUNNING, append {@code EXECUTION_RESUMING}</li>
     * </ol>
     *
     * @param executionId the execution to wake
     * @return the outcome of the wake attempt
     */
    WakeResult wakeExecution(ExecutionId executionId);
}
