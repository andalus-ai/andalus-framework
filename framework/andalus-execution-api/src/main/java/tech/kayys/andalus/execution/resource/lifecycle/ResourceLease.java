package tech.kayys.andalus.execution.resource.lifecycle;

import tech.kayys.andalus.execution.resource.model.ResourceId;

import java.time.Instant;
import java.util.Objects;

/**
 * Lease granting time-bounded reservation of a physical resource.
 */
public record ResourceLease(
        ResourceId resourceId,
        String leaseId,
        Instant acquiredAt,
        Instant expiresAt
) {
    public ResourceLease {
        Objects.requireNonNull(resourceId, "resourceId cannot be null");
        Objects.requireNonNull(leaseId, "leaseId cannot be null");
        if (acquiredAt == null) {
            acquiredAt = Instant.now();
        }
        Objects.requireNonNull(expiresAt, "expiresAt cannot be null");
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
