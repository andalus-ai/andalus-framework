package tech.kayys.andalus.agent.orchestration.coordination;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

/**
 * Coordinator for rendezvous synchronization across autonomous agent swarms.
 */
public interface DistributedBarrierCoordinator {

    /**
     * Initializes a new rendezvous barrier.
     */
    BarrierStatus createBarrier(BarrierId id, Set<String> participantAgentIds, Duration timeout);

    /**
     * Signals an agent's arrival at the barrier.
     */
    BarrierStatus arrive(BarrierId id, String agentId);

    /**
     * Retrieves current barrier status.
     */
    Optional<BarrierStatus> getStatus(BarrierId id);
}
