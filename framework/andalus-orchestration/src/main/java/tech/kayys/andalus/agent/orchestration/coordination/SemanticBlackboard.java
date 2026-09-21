package tech.kayys.andalus.agent.orchestration.coordination;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Multi-agent semantic blackboard enabling decoupled communication and atomic claiming.
 */
public interface SemanticBlackboard {

    /**
     * Posts a new entry to the blackboard topic.
     */
    BlackboardEntry post(String topic, String authorAgentId, String schemaUri, String payloadJson);

    /**
     * Retrieves an entry by its ID.
     */
    Optional<BlackboardEntry> get(String entryId);

    /**
     * Lists entries for a specific topic.
     */
    List<BlackboardEntry> getByTopic(String topic);

    /**
     * Registers a listener for updates on a specific topic.
     */
    AutoCloseable subscribe(String topic, Consumer<BlackboardEntry> listener);

    /**
     * Atomically claims an entry with a lease timeout. Returns true if acquired.
     */
    boolean claimEntry(String entryId, String agentId, long leaseDurationMs);

    /**
     * Releases an active claim.
     */
    void releaseClaim(String entryId, String agentId);

    /**
     * Updates an entry while holding an active claim.
     */
    BlackboardEntry updateClaimed(String entryId, String agentId, String newPayloadJson);
}
