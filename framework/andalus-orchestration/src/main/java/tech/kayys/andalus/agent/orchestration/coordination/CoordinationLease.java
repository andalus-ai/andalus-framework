package tech.kayys.andalus.agent.orchestration.coordination;

import java.time.Instant;
import java.util.Objects;

/**
 * Coordination lease granting temporary exclusive access or action rights within a multi-agent session.
 */
public record CoordinationLease(
        String leaseId,
        String resource,
        String participantId,
        long epoch,
        Instant acquiredAt,
        Instant expiresAt) {

    public CoordinationLease {
        Objects.requireNonNull(leaseId, "leaseId must not be null");
        Objects.requireNonNull(resource, "resource must not be null");
        Objects.requireNonNull(participantId, "participantId must not be null");
        acquiredAt = acquiredAt != null ? acquiredAt : Instant.now();
        expiresAt = expiresAt != null ? expiresAt : acquiredAt.plusSeconds(60);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public static CoordinationLease of(String leaseId, String resource, String participantId, long epoch, long ttlSeconds) {
        Instant now = Instant.now();
        return new CoordinationLease(leaseId, resource, participantId, epoch, now, now.plusSeconds(ttlSeconds));
    }
}
