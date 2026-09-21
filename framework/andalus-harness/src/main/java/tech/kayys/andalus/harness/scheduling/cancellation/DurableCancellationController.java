package tech.kayys.andalus.harness.scheduling.cancellation;

import tech.kayys.andalus.harness.execution.lifecycle.DurableExecutionControl;
import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.util.Objects;

/**
 * Controller SPI implementation delegating cancellation requests to durable execution control.
 */
public class DurableCancellationController implements CancellationController {

    private final DurableExecutionControl durableControl;

    public DurableCancellationController(DurableExecutionControl durableControl) {
        this.durableControl = Objects.requireNonNull(durableControl, "durableControl");
    }

    @Override
    public CancellationHandle request(ExecutionId executionId, CancellationMode mode) {
        Objects.requireNonNull(executionId, "executionId");
        CancellationMode effectiveMode = mode != null ? mode : CancellationMode.IMMEDIATE;
        return durableControl.cancel(executionId, effectiveMode, "Cancellation requested via CancellationController");
    }
}
