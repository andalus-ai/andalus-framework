package tech.kayys.wayang.harness.consistency.state;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a runtime execution instance.
 */
public record ExecutionId(String value) {

    public ExecutionId {
        Objects.requireNonNull(value, "ExecutionId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ExecutionId value cannot be blank");
        }
    }

    public static ExecutionId of(String value) {
        return new ExecutionId(value);
    }

    public static ExecutionId generate() {
        return new ExecutionId("exec-" + UUID.randomUUID());
    }
}
