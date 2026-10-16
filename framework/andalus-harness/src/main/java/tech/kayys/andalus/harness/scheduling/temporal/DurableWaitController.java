package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

/**
 * Controller interface for durable WAITING lifecycle coordination (6.15.5.3).
 *
 * <p>Separates the high-level intent of parking, waking, and canceling wait states
 * from internal scheduling details.</p>
 */
public interface DurableWaitController {

    /**
     * Parks the given execution waiting for the specified condition.
     * Releases worker threads without blocking (6.15.5.3).
     *
     * @param executionId the execution to park
     * @param condition   the wake condition
     */
    void waitFor(ExecutionId executionId, WakeCondition condition);

    /**
     * Wakes the given execution, validating its durable state and transitioning it toward RUNNING.
     *
     * @param executionId the execution to wake
     */
    void wake(ExecutionId executionId);

    /**
     * Cancels any active transient wake registration for this execution.
     *
     * @param executionId the execution whose wake registration should be cancelled
     */
    void cancelWake(ExecutionId executionId);
}
