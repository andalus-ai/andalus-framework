package tech.kayys.wayang.harness.isolation;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable reference record implementing {@link ResourceAllocation}.
 */
public record DefaultResourceAllocation(
        LeaseId leaseId,
        EnvironmentId environmentId,
        Set<ResourceRequirement> granted,
        Instant expiresAt,
        LeaseState state
) implements ResourceAllocation {

    public DefaultResourceAllocation {
        Objects.requireNonNull(leaseId, "LeaseId cannot be null");
        Objects.requireNonNull(environmentId, "EnvironmentId cannot be null");
        granted = granted != null ? Set.copyOf(granted) : Set.of();
        expiresAt = expiresAt != null ? expiresAt : Instant.now().plus(Duration.ofMinutes(15));
        state = state != null ? state : LeaseState.ACTIVE;
    }

    public static DefaultResourceAllocation allocate(
            EnvironmentId environmentId,
            Set<ResourceRequirement> requirements,
            Duration leaseDuration
    ) {
        return new DefaultResourceAllocation(
                LeaseId.generate(),
                environmentId,
                requirements,
                Instant.now().plus(leaseDuration != null ? leaseDuration : Duration.ofMinutes(15)),
                LeaseState.ACTIVE
        );
    }

    @Override
    public ResourceAllocation renew(Duration extension) {
        Objects.requireNonNull(extension, "Extension duration cannot be null");
        return new DefaultResourceAllocation(
                leaseId,
                environmentId,
                granted,
                expiresAt.plus(extension),
                LeaseState.ACTIVE
        );
    }

    @Override
    public ResourceAllocation release() {
        return new DefaultResourceAllocation(
                leaseId,
                environmentId,
                granted,
                expiresAt,
                LeaseState.RELEASED
        );
    }

    @Override
    public ResourceAllocation revoke() {
        return new DefaultResourceAllocation(
                leaseId,
                environmentId,
                granted,
                expiresAt,
                LeaseState.REVOKING
        );
    }
}
