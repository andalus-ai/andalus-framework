package tech.kayys.wayang.execution.environment;

import java.time.Instant;
import java.util.Set;

/**
 * Declares granted resource allocation and lease timing for an execution environment.
 */
public record ResourceAllocation(
        String leaseId,
        Set<ResourceRequirement> grantedResources,
        Instant allocatedAt,
        Instant expiresAt,
        boolean active
) {

    public ResourceAllocation {
        grantedResources = grantedResources != null ? Set.copyOf(grantedResources) : Set.of();
        allocatedAt = allocatedAt != null ? allocatedAt : Instant.now();
        expiresAt = expiresAt != null ? expiresAt : Instant.MAX;
    }

    public static ResourceAllocation unconstrained() {
        return new ResourceAllocation("lease-unconstrained", Set.of(), Instant.now(), Instant.MAX, true);
    }
}
