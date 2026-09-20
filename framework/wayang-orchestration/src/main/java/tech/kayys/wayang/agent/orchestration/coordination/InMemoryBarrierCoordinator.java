package tech.kayys.wayang.agent.orchestration.coordination;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory barrier coordinator implementation.
 */
public class InMemoryBarrierCoordinator implements DistributedBarrierCoordinator {

    private final Map<BarrierId, BarrierStatus> barriers = new ConcurrentHashMap<>();

    @Override
    public BarrierStatus createBarrier(BarrierId id, Set<String> participantAgentIds, Duration timeout) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(participantAgentIds, "participantAgentIds cannot be null");
        Objects.requireNonNull(timeout, "timeout cannot be null");

        Instant now = Instant.now();
        BarrierStatus status = new BarrierStatus(
            id,
            participantAgentIds,
            Set.of(),
            false,
            false,
            now,
            now.plus(timeout)
        );
        barriers.put(id, status);
        return status;
    }

    @Override
    public BarrierStatus arrive(BarrierId id, String agentId) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(agentId, "agentId cannot be null");

        return barriers.compute(id, (barrierId, current) -> {
            if (current == null) {
                throw new IllegalArgumentException("Barrier not found: " + id);
            }

            Instant now = Instant.now();
            boolean timedOut = now.isAfter(current.expiresAt());

            Set<String> updatedArrived = new HashSet<>(current.arrivedParticipants());
            if (current.requiredParticipants().contains(agentId)) {
                updatedArrived.add(agentId);
            }

            boolean released = !timedOut && updatedArrived.containsAll(current.requiredParticipants());

            return new BarrierStatus(
                current.id(),
                current.requiredParticipants(),
                updatedArrived,
                released,
                timedOut,
                current.createdAt(),
                current.expiresAt()
            );
        });
    }

    @Override
    public Optional<BarrierStatus> getStatus(BarrierId id) {
        Objects.requireNonNull(id, "id cannot be null");
        BarrierStatus status = barriers.get(id);
        if (status == null) {
            return Optional.empty();
        }

        // Check if timed out in the interim
        if (!status.isReleased() && !status.isTimedOut() && Instant.now().isAfter(status.expiresAt())) {
            status = new BarrierStatus(
                status.id(),
                status.requiredParticipants(),
                status.arrivedParticipants(),
                false,
                true,
                status.createdAt(),
                status.expiresAt()
            );
            barriers.put(id, status);
        }

        return Optional.of(status);
    }
}
