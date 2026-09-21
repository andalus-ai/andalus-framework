package tech.kayys.andalus.execution.mobility;

import java.time.Duration;
import java.util.Optional;

/**
 * Manages distributed execution leases and generation-based fencing tokens.
 */
public interface LeaseManager {

    /**
     * Acquire a lease for the given execution and holder.
     */
    ExecutionLease acquireLease(String executionId, String holderId, Duration duration);

    /**
     * Renew an existing lease.
     */
    boolean renewLease(String executionId, String holderId, Duration duration);

    /**
     * Release a lease held by a runtime.
     */
    void releaseLease(String executionId, String holderId);

    /**
     * Validate that a fencing token is current for an execution.
     */
    boolean validateToken(String executionId, FencingToken token);

    /**
     * Get the current fencing token for an execution.
     */
    Optional<FencingToken> currentToken(String executionId);

    /**
     * Get the active lease for an execution if present.
     */
    Optional<ExecutionLease> getLease(String executionId);
}
