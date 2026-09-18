package tech.kayys.wayang.harness.consistency.state;

import java.util.Objects;
import java.util.Optional;

/**
 * Result of attempting an execution state transition.
 */
public record TransitionResult(
        boolean success,
        Optional<ExecutionRecord> record,
        String message
) {

    public TransitionResult {
        record = record != null ? record : Optional.empty();
        message = message != null ? message : "";
    }

    public static TransitionResult success(ExecutionRecord record) {
        return new TransitionResult(true, Optional.of(Objects.requireNonNull(record)), "State transitioned successfully");
    }

    public static TransitionResult rejected(String reason) {
        return new TransitionResult(false, Optional.empty(), reason);
    }
}
