package tech.kayys.wayang.execution.environment;

import java.util.Objects;

/**
 * Requirement specification for a specific resource type and quantity.
 */
public record ResourceRequirement(
        String resourceType,
        double amount,
        String unit
) {

    public ResourceRequirement {
        Objects.requireNonNull(resourceType, "resourceType cannot be null");
        unit = unit != null ? unit : "";
    }

    public static ResourceRequirement cpu(double cores) {
        return new ResourceRequirement("cpu", cores, "cores");
    }

    public static ResourceRequirement memoryMb(double megabytes) {
        return new ResourceRequirement("memory", megabytes, "MB");
    }

    public static ResourceRequirement gpu(int count) {
        return new ResourceRequirement("gpu", count, "device");
    }
}
