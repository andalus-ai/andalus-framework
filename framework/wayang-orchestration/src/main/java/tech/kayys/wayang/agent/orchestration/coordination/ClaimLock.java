package tech.kayys.wayang.agent.orchestration.coordination;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A temporary lease claimed by an agent on a blackboard entry to prevent concurrent edits.
 */
public record ClaimLock(
    String entryId,
    String ownerAgentId,
    long leaseDurationMs,
    Instant acquiredAt
) implements Serializable {

    public ClaimLock {
        Objects.requireNonNull(entryId, "entryId cannot be null");
        Objects.requireNonNull(ownerAgentId, "ownerAgentId cannot be null");
        Objects.requireNonNull(acquiredAt, "acquiredAt cannot be null");
        if (leaseDurationMs <= 0) {
            throw new IllegalArgumentException("leaseDurationMs must be positive");
        }
    }

    public boolean isExpired() {
        return Instant.now().isAfter(acquiredAt.plusMillis(leaseDurationMs));
    }
}
