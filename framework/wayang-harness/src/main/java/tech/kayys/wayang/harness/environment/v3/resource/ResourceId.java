package tech.kayys.wayang.harness.environment.v3.resource;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a hardware, network, or logical resource.
 */
public record ResourceId(String value) {
    public ResourceId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ResourceId cannot be blank");
        }
    }

    public static ResourceId of(String value) {
        return new ResourceId(value);
    }

    public static ResourceId generate() {
        return new ResourceId("res-" + UUID.randomUUID());
    }
}
