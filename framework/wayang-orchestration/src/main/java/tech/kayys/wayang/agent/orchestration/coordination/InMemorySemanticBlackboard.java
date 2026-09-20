package tech.kayys.wayang.agent.orchestration.coordination;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Thread-safe in-memory implementation of {@link SemanticBlackboard}.
 */
public class InMemorySemanticBlackboard implements SemanticBlackboard {

    private final Map<String, BlackboardEntry> entries = new ConcurrentHashMap<>();
    private final Map<String, ClaimLock> claims = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<BlackboardEntry>>> topicSubscribers = new ConcurrentHashMap<>();

    @Override
    public BlackboardEntry post(String topic, String authorAgentId, String schemaUri, String payloadJson) {
        BlackboardEntry entry = BlackboardEntry.of(topic, authorAgentId, schemaUri, payloadJson);
        entries.put(entry.entryId(), entry);
        notifySubscribers(entry);
        return entry;
    }

    @Override
    public Optional<BlackboardEntry> get(String entryId) {
        Objects.requireNonNull(entryId, "entryId cannot be null");
        return Optional.ofNullable(entries.get(entryId));
    }

    @Override
    public List<BlackboardEntry> getByTopic(String topic) {
        Objects.requireNonNull(topic, "topic cannot be null");
        List<BlackboardEntry> result = new ArrayList<>();
        for (BlackboardEntry entry : entries.values()) {
            if (topic.equals(entry.topic())) {
                result.add(entry);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public AutoCloseable subscribe(String topic, Consumer<BlackboardEntry> listener) {
        Objects.requireNonNull(topic, "topic cannot be null");
        Objects.requireNonNull(listener, "listener cannot be null");
        List<Consumer<BlackboardEntry>> list = topicSubscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>());
        list.add(listener);
        return () -> list.remove(listener);
    }

    @Override
    public boolean claimEntry(String entryId, String agentId, long leaseDurationMs) {
        Objects.requireNonNull(entryId, "entryId cannot be null");
        Objects.requireNonNull(agentId, "agentId cannot be null");

        if (!entries.containsKey(entryId)) {
            throw new IllegalArgumentException("Entry not found: " + entryId);
        }

        ClaimLock newLock = new ClaimLock(entryId, agentId, leaseDurationMs, Instant.now());
        ClaimLock existing = claims.compute(entryId, (id, current) -> {
            if (current == null || current.isExpired()) {
                return newLock;
            }
            if (current.ownerAgentId().equals(agentId)) {
                // Refresh lease
                return newLock;
            }
            return current;
        });

        return existing == newLock;
    }

    @Override
    public void releaseClaim(String entryId, String agentId) {
        Objects.requireNonNull(entryId, "entryId cannot be null");
        Objects.requireNonNull(agentId, "agentId cannot be null");

        claims.computeIfPresent(entryId, (id, current) -> {
            if (current.ownerAgentId().equals(agentId)) {
                return null;
            }
            return current;
        });
    }

    @Override
    public BlackboardEntry updateClaimed(String entryId, String agentId, String newPayloadJson) {
        Objects.requireNonNull(entryId, "entryId cannot be null");
        Objects.requireNonNull(agentId, "agentId cannot be null");
        Objects.requireNonNull(newPayloadJson, "newPayloadJson cannot be null");

        ClaimLock lock = claims.get(entryId);
        if (lock == null || lock.isExpired() || !lock.ownerAgentId().equals(agentId)) {
            throw new IllegalStateException("Agent " + agentId + " does not hold a valid active claim on " + entryId);
        }

        BlackboardEntry updated = entries.compute(entryId, (id, current) -> {
            if (current == null) {
                throw new IllegalArgumentException("Entry not found: " + entryId);
            }
            return current.withPayload(newPayloadJson);
        });

        notifySubscribers(updated);
        return updated;
    }

    private void notifySubscribers(BlackboardEntry entry) {
        List<Consumer<BlackboardEntry>> subs = topicSubscribers.get(entry.topic());
        if (subs != null) {
            for (Consumer<BlackboardEntry> sub : subs) {
                try {
                    sub.accept(entry);
                } catch (Exception ignored) {}
            }
        }
    }
}
