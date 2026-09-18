package tech.kayys.wayang.harness.isolation;

import java.util.Set;

/**
 * Composite request declaring resource requirements and allocation priority.
 */
public record ResourceRequest(
        Set<ResourceRequirement> requirements,
        ResourcePriority priority
) {

    public ResourceRequest {
        requirements = requirements != null ? Set.copyOf(requirements) : Set.of();
        priority = priority != null ? priority : ResourcePriority.NORMAL;
    }

    public static ResourceRequest of(Set<ResourceRequirement> requirements) {
        return new ResourceRequest(requirements, ResourcePriority.NORMAL);
    }

    public static ResourceRequest of(Set<ResourceRequirement> requirements, ResourcePriority priority) {
        return new ResourceRequest(requirements, priority);
    }

    public static ResourceRequest minimal() {
        return new ResourceRequest(Set.of(), ResourcePriority.LOW);
    }
}
