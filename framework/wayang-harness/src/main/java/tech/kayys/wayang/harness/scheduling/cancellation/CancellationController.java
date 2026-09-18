package tech.kayys.wayang.harness.scheduling.cancellation;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

/**
 * Controller SPI for managing cancellation and drain operations across executions.
 */
public interface CancellationController {

    CancellationHandle request(ExecutionId executionId, CancellationMode mode);
}
