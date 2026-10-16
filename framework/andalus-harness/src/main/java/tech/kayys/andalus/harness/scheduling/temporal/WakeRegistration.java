package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

/**
 * Adapter interface for registering transient wake triggers (6.15.5.4).
 *
 * <p>Transports/listeners registered through this interface are delivery optimizations.
 * Durable execution state remains the authority.</p>
 */
public interface WakeRegistration {

    /**
     * Registers a wake condition for an execution.
     *
     * @param executionId the waiting execution
     * @param condition   the condition that will wake it
     */
    void register(ExecutionId executionId, WakeCondition condition);

    /**
     * Cancels any active transient wake registration for the execution.
     *
     * @param executionId the execution to cancel
     */
    void cancel(ExecutionId executionId);
}
