package tech.kayys.wayang.harness.scheduling.cancellation;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

/**
 * Handle representing an in-flight or completed cancellation request.
 */
public interface CancellationHandle {

    ExecutionId executionId();

    CancellationMode mode();

    boolean isCanceled();
}
