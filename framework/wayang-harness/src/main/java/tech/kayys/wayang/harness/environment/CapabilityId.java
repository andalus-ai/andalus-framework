package tech.kayys.wayang.harness.environment;

import java.util.Objects;

/**
 * Strongly typed identifier for a harness capability.
 */
public record CapabilityId(String value) {

    public CapabilityId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Capability id must not be blank");
        }
    }

    public static CapabilityId of(String value) {
        return new CapabilityId(value);
    }
}
