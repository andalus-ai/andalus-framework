package tech.kayys.wayang.harness.execution.recovery;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.execution.state.ExecutionState;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record RecoveryContext(
        ExecutionId executionId,
        Optional<ExecutionState> lastState,
        Optional<Throwable> failure,
        Map<String, Object> metadata
) {
    public RecoveryContext {
        Objects.requireNonNull(executionId, "executionId");
        lastState = lastState == null ? Optional.empty() : lastState;
        failure = failure == null ? Optional.empty() : failure;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static RecoveryContext of(ExecutionId executionId, ExecutionState state, Throwable failure) {
        return new RecoveryContext(executionId, Optional.ofNullable(state), Optional.ofNullable(failure), Map.of());
    }
}
