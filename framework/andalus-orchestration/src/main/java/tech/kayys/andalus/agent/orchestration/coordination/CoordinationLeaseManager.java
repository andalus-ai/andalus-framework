package tech.kayys.andalus.agent.orchestration.coordination;

import java.util.Optional;

/**
 * Coordination lease manager interface for governing resource exclusivity across participants.
 */
public interface CoordinationLeaseManager {

    /**
     * Attempts to acquire an exclusive coordination lease on a named resource.
     */
    Optional<CoordinationLease> acquire(String resource, String participantId, long ttlSeconds);

    /**
     * Releases an existing lease.
     */
    void release(CoordinationLease lease);

    /**
     * Queries the active lease for a named resource, if present and not expired.
     */
    Optional<CoordinationLease> activeLease(String resource);
}
