package tech.kayys.wayang.harness.environment.v3;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for an execution environment.
 */
public record EnvironmentId(String value) {
    public EnvironmentId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("EnvironmentId cannot be blank");
        }
    }

    public static EnvironmentId of(String value) {
        return new EnvironmentId(value);
    }

    public static EnvironmentId generate() {
        return new EnvironmentId("env-" + UUID.randomUUID());
    }
}
