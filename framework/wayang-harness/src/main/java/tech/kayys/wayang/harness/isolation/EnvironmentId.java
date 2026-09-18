package tech.kayys.wayang.harness.isolation;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique logical identifier for an execution environment.
 */
public record EnvironmentId(String value) {

    public EnvironmentId {
        Objects.requireNonNull(value, "EnvironmentId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("EnvironmentId value cannot be blank");
        }
    }

    public static EnvironmentId of(String value) {
        return new EnvironmentId(value);
    }

    public static EnvironmentId generate() {
        return new EnvironmentId("env-" + UUID.randomUUID());
    }
}
