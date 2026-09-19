package tech.kayys.wayang.execution.lifecycle;

import java.util.Objects;
import java.util.Optional;

public record TransitionResult(
        boolean success,
        ExecutionState state,
        Optional<String> errorMessage
) {
    public TransitionResult {
        Objects.requireNonNull(state, "state cannot be null");
    }

    public static TransitionResult success(ExecutionState state) {
        return new TransitionResult(true, state, Optional.empty());
    }

    public static TransitionResult failure(ExecutionState currentState, String reason) {
        return new TransitionResult(false, currentState, Optional.of(reason));
    }
}
