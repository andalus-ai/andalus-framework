package tech.kayys.wayang.harness.environment.v3.resource;

import java.util.Map;

/**
 * Specification of resource requirements for allocation.
 */
public record ResourceRequirements(
        long requiredCapacity,
        Map<String, String> constraints
) {
    public ResourceRequirements {
        constraints = constraints != null ? Map.copyOf(constraints) : Map.of();
    }

    public static ResourceRequirements of(long capacity) {
        return new ResourceRequirements(capacity, Map.of());
    }

    public static ResourceRequirements none() {
        return new ResourceRequirements(0L, Map.of());
    }
}
