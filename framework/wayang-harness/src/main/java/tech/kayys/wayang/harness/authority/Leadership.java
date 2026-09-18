package tech.kayys.wayang.harness.authority;

import java.time.Instant;

/**
 * Representation of an active leadership tenure.
 */
public interface Leadership {

    LeadershipId id();

    CoordinatorId coordinator();

    CoordinationEpoch epoch();

    Instant acquiredAt();

    Instant expiresAt();

    boolean isValid();
}
