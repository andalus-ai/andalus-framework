package tech.kayys.andalus.agent.orchestration.coordination;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Thread-safe in-memory implementation of {@link SemanticBlackboard}.
 *
 * <p>Claim lifecycle operations are synchronized as a unit so a claim cannot
 * be released or replaced between authorization and a claimed update. This
 * implementation coordinates threads within one process; distributed
 * deployments must provide a shared implementation of the interface.</p>
 */
public class InMemorySemanticBlackboard implements SemanticBlackboard {

    private final Map<String, BlackboardEntry> entries = new ConcurrentHashMap<>();
    private final Map<String, ClaimLock> claims = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<BlackboardEntry>>> topicSubscribers = new ConcurrentHashMap<>();

    @Override
    public BlackboardEntry post(String topic, String authorAgentId, String schemaUri, String payloadJson) {
        requireText(topic, "topic");
        requireText(authorAgentId, "authorAgentId");
        Objects.requireNonNull(payloadJson, "payloadJson cannot be null");
        BlackboardEntry entry = BlackboardEntry.of(topic, authorAgentId, schemaUri, payloadJson);
        entries.put(entry.entryId(), entry);
        notifySubscribers(entry);
        return entry;
    }

    @Override
    public Optional<BlackboardEntry> get(String entryId) {
        requireText(entryId, "entryId");
        return Optional.ofNullable(entries.get(entryId));
    }

    @Override
    public List<BlackboardEntry> getByTopic(String topic) {
        requireText(topic, "topic");
        List<BlackboardEntry> result = new ArrayList<>();
        for (BlackboardEntry entry : entries.values()) {
            if (topic.equals(entry.topic())) {
                result.add(entry);
            }
        }
        result.sort(Comparator.comparing(BlackboardEntry::publishedAt)
                .thenComparing(BlackboardEntry::entryId));
        return List.copyOf(result);
    }

    @Override
    public AutoCloseable subscribe(String topic, Consumer<BlackboardEntry> listener) {
        requireText(topic, "topic");
        Objects.requireNonNull(listener, "listener cannot be null");
        List<Consumer<BlackboardEntry>> subscribers =
                topicSubscribers.computeIfAbsent(topic, ignored -> new CopyOnWriteArrayList<>());
        subscribers.add(listener);
        return () -> topicSubscribers.computeIfPresent(topic, (key, current) -> {
            current.remove(listener);
            return current.isEmpty() ? null : current;
        });
    }

    @Override
    public synchronized boolean claimEntry(String entryId, String agentId, long leaseDurationMs) {
        requireText(entryId, "entryId");
        requireText(agentId, "agentId");
        if (leaseDurationMs <= 0) {
            throw new IllegalArgumentException("leaseDurationMs must be positive");
        }
        if (!entries.containsKey(entryId)) {
            throw new IllegalArgumentException("Entry not found: " + entryId);
        }

        ClaimLock current = claims.get(entryId);
        if (current != null && !current.isExpired() && !current.ownerAgentId().equals(agentId)) {
            return false;
        }

        claims.put(entryId, new ClaimLock(entryId, agentId, leaseDurationMs, Instant.now()));
        return true;
    }

    @Override
    public synchronized void releaseClaim(String entryId, String agentId) {
        requireText(entryId, "entryId");
        requireText(agentId, "agentId");
        ClaimLock current = claims.get(entryId);
        if (current != null && current.ownerAgentId().equals(agentId)) {
            claims.remove(entryId, current);
        }
    }

    @Override
    public synchronized BlackboardEntry updateClaimed(String entryId, String agentId, String newPayloadJson) {
        requireText(entryId, "entryId");
        requireText(agentId, "agentId");
        Objects.requireNonNull(newPayloadJson, "newPayloadJson cannot be null");

        ClaimLock lock = claims.get(entryId);
        if (lock == null || lock.isExpired() || !lock.ownerAgentId().equals(agentId)) {
            throw new IllegalStateException(
                    "Agent " + agentId + " does not hold a valid active claim on " + entryId);
        }

        BlackboardEntry current = entries.get(entryId);
        if (current == null) {
            throw new IllegalArgumentException("Entry not found: " + entryId);
        }
        BlackboardEntry updated = current.withPayload(newPayloadJson);
        entries.put(entryId, updated);
        notifySubscribers(updated);
        return updated;
    }

    private void notifySubscribers(BlackboardEntry entry) {
        List<Consumer<BlackboardEntry>> subscribers = topicSubscribers.get(entry.topic());
        if (subscribers != null) {
            for (Consumer<BlackboardEntry> subscriber : subscribers) {
                try {
                    subscriber.accept(entry);
                } catch (Exception ignored) {
                    // A subscriber must not prevent delivery to other subscribers or publishing.
                }
            }
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
