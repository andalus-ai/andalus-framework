package tech.kayys.andalus.harness.scheduling.cancellation;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.util.Objects;

/**
 * Default immutable implementation of {@link CancellationHandle}.
 */
public record DefaultCancellationHandle(
        ExecutionId executionId,
        CancellationMode mode,
        boolean isCanceled
) implements CancellationHandle {

    public DefaultCancellationHandle {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(mode, "mode");
    }

    public static DefaultCancellationHandle accepted(ExecutionId executionId, CancellationMode mode) {
        return new DefaultCancellationHandle(executionId, mode, true);
    }

    public static DefaultCancellationHandle alreadyTerminal(ExecutionId executionId, CancellationMode mode) {
        return new DefaultCancellationHandle(executionId, mode, false);
    }
}
