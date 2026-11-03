package tech.kayys.andalus.execution.mobility;

import java.time.Duration;

/**
 * Lease manager operations that require the caller's exact fencing generation.
 *
 * <p>Implementations must compare holder and generation and perform the mutation
 * atomically. A check followed by an unfenced mutation is not a valid implementation.
 * Distributed implementations must use an atomic shared coordinator.</p>
 */
public interface FencedLeaseManager extends LeaseManager {

    /**
     * Renews only the currently active lease matching both holder and fencing token.
     * The generation is retained; an expired or superseded token is rejected.
     */
    boolean renewLease(String executionId, String holderId, FencingToken token, Duration duration);

    /**
     * Releases only the currently active lease matching both holder and fencing token.
     * A stale owner must not be able to release a successor's lease.
     */
    boolean releaseLease(String executionId, String holderId, FencingToken token);
}
