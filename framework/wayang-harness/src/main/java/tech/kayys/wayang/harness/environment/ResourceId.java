package tech.kayys.wayang.harness.environment;

import java.util.Objects;

/**
 * Strongly typed identifier for an environment resource.
 */
public record ResourceId(String value) {

    public ResourceId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Resource id must not be blank");
        }
    }

    public static ResourceId of(String value) {
        return new ResourceId(value);
    }
}
