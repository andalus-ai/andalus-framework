package tech.kayys.wayang.execution.lifecycle;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record ExecutionTransition(
        ExecutionState from,
        ExecutionState to,
        String reason,
        Instant timestamp,
        Map<String, Object> metadata
) {
    public ExecutionTransition {
        Objects.requireNonNull(from, "from cannot be null");
        Objects.requireNonNull(to, "to cannot be null");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ExecutionTransition of(ExecutionState from, ExecutionState to, String reason) {
        return new ExecutionTransition(from, to, reason, Instant.now(), Map.of());
    }
}
