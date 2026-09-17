package tech.kayys.wayang.harness.resource;

import java.util.Objects;

/**
 * Request describing a desired resource allocation and its constraints.
 */
public record ResourceRequest(
        ResourceType type,
        String resourceId,
        ResourceConstraints constraints
) {

    public ResourceRequest {
        Objects.requireNonNull(type, "type");
        constraints = constraints == null ? ResourceConstraints.unconstrained() : constraints;
    }

    public static ResourceRequest of(ResourceType type) {
        return new ResourceRequest(type, null, ResourceConstraints.unconstrained());
    }

    public static ResourceRequest of(ResourceType type, String resourceId) {
        return new ResourceRequest(type, resourceId, ResourceConstraints.unconstrained());
    }

    public static ResourceRequest of(ResourceType type, String resourceId, ResourceConstraints constraints) {
        return new ResourceRequest(type, resourceId, constraints);
    }
}
