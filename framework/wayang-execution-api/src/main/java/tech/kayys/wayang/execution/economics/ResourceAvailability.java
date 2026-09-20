package tech.kayys.wayang.execution.economics;

import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.util.Objects;

/**
 * Current availability breakdown for a resource type.
 */
public record ResourceAvailability(
    ResourceType type,
    double availableUnits,
    double reservedUnits,
    double allocatedUnits
) {
    public ResourceAvailability {
        Objects.requireNonNull(type, "type cannot be null");
    }

    public static ResourceAvailability of(ResourceType type, double available, double reserved, double allocated) {
        return new ResourceAvailability(type, available, reserved, allocated);
    }
}
