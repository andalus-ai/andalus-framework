package tech.kayys.wayang.harness.isolation;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

/**
 * Contract representing an active, revocable resource allocation bound to an environment.
 */
public interface ResourceAllocation {

    LeaseId leaseId();

    EnvironmentId environmentId();

    Set<ResourceRequirement> granted();

    Instant expiresAt();

    LeaseState state();

    ResourceAllocation renew(Duration extension);

    ResourceAllocation release();

    ResourceAllocation revoke();
}
