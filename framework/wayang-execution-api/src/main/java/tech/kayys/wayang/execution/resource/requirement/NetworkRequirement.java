package tech.kayys.wayang.execution.resource.requirement;

import tech.kayys.wayang.execution.resource.model.ResourceId;
import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.util.List;
import java.util.Objects;

/**
 * Network egress and domain policy requirement specification.
 */
public record NetworkRequirement(
        ResourceId resourceId,
        boolean egressAllowed,
        List<String> allowedHosts
) implements ResourceRequirement {
    public NetworkRequirement {
        Objects.requireNonNull(resourceId, "resourceId cannot be null");
        allowedHosts = allowedHosts == null ? List.of() : List.copyOf(allowedHosts);
    }

    @Override
    public ResourceType type() {
        return ResourceType.NETWORK;
    }

    public static NetworkRequirement isolated(ResourceId id) {
        return new NetworkRequirement(id, false, List.of());
    }

    public static NetworkRequirement allowed(ResourceId id, List<String> hosts) {
        return new NetworkRequirement(id, true, hosts);
    }
}
