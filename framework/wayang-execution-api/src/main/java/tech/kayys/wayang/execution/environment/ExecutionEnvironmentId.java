package tech.kayys.wayang.execution.environment;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable identifier for an execution environment.
 */
public record ExecutionEnvironmentId(String value) {

    public ExecutionEnvironmentId {
        Objects.requireNonNull(value, "ExecutionEnvironmentId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ExecutionEnvironmentId cannot be blank");
        }
        value = value.trim();
    }

    public static ExecutionEnvironmentId of(String value) {
        return new ExecutionEnvironmentId(value);
    }

    public static ExecutionEnvironmentId generate() {
        return new ExecutionEnvironmentId("env-" + UUID.randomUUID());
    }
}
