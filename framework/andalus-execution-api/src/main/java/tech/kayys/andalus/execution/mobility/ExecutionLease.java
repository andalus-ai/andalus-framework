package tech.kayys.andalus.execution.mobility;

import java.time.Instant;
import java.util.Objects;

/**
 * Execution lease establishing exclusive runtime execution rights.
 *
 * <p>The expiry helpers on this value object are local-time conveniences only.
 * They cannot establish authoritative lease validity when the lease manager uses
 * a shared or database clock. Callers making ownership or mutation decisions must
 * validate the fencing token with the authoritative {@link LeaseManager}.</p>
 */
public record ExecutionLease(
        String executionId,
        String holderId,
        FencingToken fencingToken,
        Instant acquiredAt,
        Instant expiresAt
) {
    public ExecutionLease {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(holderId, "holderId cannot be null");
        Objects.requireNonNull(fencingToken, "fencingToken cannot be null");
        if (acquiredAt == null) {
            acquiredAt = Instant.now();
        }
        Objects.requireNonNull(expiresAt, "expiresAt cannot be null");
    }

    /**
     * Estimates expiry using the local JVM clock.
     *
     * <p>This method is not authoritative for leases managed using a database
     * or other shared clock. Use the lease manager to validate ownership.</p>
     */
    public boolean isExpired() {
        return isExpired(Instant.now());
    }

    /**
     * Evaluates expiry against an explicitly supplied instant.
     * The lease is expired at the exact expiry boundary.
     */
    public boolean isExpired(Instant now) {
        Objects.requireNonNull(now, "now cannot be null");
        return !now.isBefore(expiresAt);
    }

    /**
     * Performs a local-time convenience check for holder and token.
     * This is not a substitute for authoritative manager-side validation.
     */
    public boolean isValidFor(String holder, FencingToken token) {
        return isValidFor(holder, token, Instant.now());
    }

    /**
     * Evaluates holder, token and expiry against an explicitly supplied instant.
     */
    public boolean isValidFor(String holder, FencingToken token, Instant now) {
        Objects.requireNonNull(now, "now cannot be null");
        return holderId.equals(holder) && fencingToken.equals(token) && !isExpired(now);
    }
}
