package tech.kayys.andalus.harness.scheduling.cancellation;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

/**
 * Handle representing an in-flight or completed cancellation request.
 */
public interface CancellationHandle {

    ExecutionId executionId();

    CancellationMode mode();

    boolean isCanceled();
}
