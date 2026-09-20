package tech.kayys.wayang.execution.mobility;

import java.time.Instant;
import java.util.Objects;

/**
 * Execution lease establishing exclusive runtime execution rights.
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

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValidFor(String holder, FencingToken token) {
        return !isExpired() && holderId.equals(holder) && fencingToken.equals(token);
    }
}
