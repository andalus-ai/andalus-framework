package tech.kayys.wayang.execution.resource.requirement;

import tech.kayys.wayang.execution.resource.model.ResourceId;
import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.util.Objects;

/**
 * Model inference requirement specification.
 */
public record ModelRequirement(
        ResourceId resourceId,
        String capability,
        int minContextWindow,
        String preferredProvider
) implements ResourceRequirement {
    public ModelRequirement {
        Objects.requireNonNull(resourceId, "resourceId cannot be null");
        Objects.requireNonNull(capability, "capability cannot be null");
        if (minContextWindow <= 0) {
            minContextWindow = 8192;
        }
        preferredProvider = preferredProvider == null ? "any" : preferredProvider;
    }

    @Override
    public ResourceType type() {
        return ResourceType.MODEL;
    }

    public static ModelRequirement of(ResourceId id, String capability) {
        return new ModelRequirement(id, capability, 8192, "any");
    }
}
