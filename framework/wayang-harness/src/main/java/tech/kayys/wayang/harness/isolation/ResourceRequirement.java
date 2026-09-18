package tech.kayys.wayang.harness.isolation;

import java.util.Objects;

/**
 * Concrete resource demand declared by an operation or task.
 */
public record ResourceRequirement(ResourceType type, Quantity requested) {

    public ResourceRequirement {
        Objects.requireNonNull(type, "ResourceType cannot be null");
        Objects.requireNonNull(requested, "Quantity cannot be null");
    }

    public static ResourceRequirement of(ResourceType type, Quantity requested) {
        return new ResourceRequirement(type, requested);
    }
}
