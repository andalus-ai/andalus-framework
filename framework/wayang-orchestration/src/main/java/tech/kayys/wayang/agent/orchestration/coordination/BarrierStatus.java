package tech.kayys.wayang.agent.orchestration.coordination;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Status snapshot of a distributed synchronization barrier.
 */
public record BarrierStatus(
    BarrierId id,
    Set<String> requiredParticipants,
    Set<String> arrivedParticipants,
    boolean isReleased,
    boolean isTimedOut,
    Instant createdAt,
    Instant expiresAt
) implements Serializable {

    public BarrierStatus {
        Objects.requireNonNull(id, "id cannot be null");
        requiredParticipants = requiredParticipants != null ? Set.copyOf(requiredParticipants) : Set.of();
        arrivedParticipants = arrivedParticipants != null ? Set.copyOf(arrivedParticipants) : Set.of();
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
        Objects.requireNonNull(expiresAt, "expiresAt cannot be null");
    }

    public boolean isSatisfied() {
        return arrivedParticipants.containsAll(requiredParticipants);
    }
}
