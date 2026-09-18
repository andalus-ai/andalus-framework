package tech.kayys.wayang.harness.environment.v3.resource;

import java.util.Objects;

/**
 * Request to acquire or allocate a resource under governance.
 */
public record ResourceRequest(
        ResourceType type,
        ResourceRequirements requirements,
        ResourceAccessMode accessMode
) {
    public ResourceRequest {
        Objects.requireNonNull(type, "type");
        requirements = requirements != null ? requirements : ResourceRequirements.none();
        accessMode = accessMode != null ? accessMode : ResourceAccessMode.SHARED;
    }

    public static ResourceRequest of(ResourceType type) {
        return new ResourceRequest(type, ResourceRequirements.none(), ResourceAccessMode.SHARED);
    }

    public static ResourceRequest of(ResourceType type, ResourceAccessMode accessMode) {
        return new ResourceRequest(type, ResourceRequirements.none(), accessMode);
    }

    public static ResourceRequest of(ResourceType type, ResourceRequirements requirements, ResourceAccessMode accessMode) {
        return new ResourceRequest(type, requirements, accessMode);
    }
}
