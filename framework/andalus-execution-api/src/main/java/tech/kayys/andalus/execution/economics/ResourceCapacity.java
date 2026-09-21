package tech.kayys.andalus.execution.economics;

import tech.kayys.andalus.execution.resource.model.ResourceType;

import java.util.Objects;

/**
 * Total provisioned capacity of a specific resource type.
 */
public record ResourceCapacity(
    ResourceType type,
    double totalUnits,
    String unitOfMeasure
) {
    public ResourceCapacity {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(unitOfMeasure, "unitOfMeasure cannot be null");
    }

    public static ResourceCapacity of(ResourceType type, double units, String uom) {
        return new ResourceCapacity(type, units, uom);
    }
}
