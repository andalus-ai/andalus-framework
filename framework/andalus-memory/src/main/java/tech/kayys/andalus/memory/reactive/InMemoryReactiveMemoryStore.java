package tech.kayys.andalus.memory.reactive;

import tech.kayys.andalus.memory.MemoryRecord;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * In-memory reactive memory store.
 * Suitable for single-node usage and testing.
 * In production, replace with a persistent/distributed implementation.
 */
public final class InMemoryReactiveMemoryStore implements ReactiveMemoryStore {

    private final ConcurrentMap<String, MemoryRecord> records = new ConcurrentHashMap<>();
    private final List<MemoryEventListener> listeners = new CopyOnWriteArrayList<>();
    private final SubmissionPublisher<MemoryEvent> publisher;
    private final Executor executor;

    public InMemoryReactiveMemoryStore() {
        this(ForkJoinPool.commonPool());
    }

    public InMemoryReactiveMemoryStore(Executor executor) {
        this.executor = executor;
        this.publisher = new SubmissionPublisher<>(executor, Flow.defaultBufferSize());
    }

    @Override
    public CompletionStage<MemoryRecord> store(MemoryRecord record, String trigger) {
        return CompletableFuture.supplyAsync(() -> {
            MemoryRecord previous = records.put(record.id(), record);
            MemoryEvent event = previous == null
                    ? new MemoryEvent.MemoryStored(record.userId(), record.sessionId(), record, trigger, Instant.now())
                    : new MemoryEvent.MemoryUpdated(record.userId(), record.sessionId(), previous, record, Instant.now());
            emit(event);
            return record;
        }, executor);
    }

    @Override
    public CompletionStage<List<MemoryRecord>> recall(
            String agentId, String sessionId, String query, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            String q = query == null ? "" : query.toLowerCase();
            var matched = records.values().stream()
                    .filter(r -> sessionId == null || sessionId.equals(r.sessionId()))
                    .filter(r -> q.isBlank() || r.key().toLowerCase().contains(q)
                            || r.value().toLowerCase().contains(q))
                    .sorted(Comparator.comparingDouble(MemoryRecord::relevance).reversed()
                            .thenComparing(Comparator.comparing(MemoryRecord::timestamp).reversed()))
                    .limit(limit > 0 ? limit : 10)
                    .toList();

            if (!matched.isEmpty()) {
                emit(new MemoryEvent.MemoryAccessed(agentId, sessionId, null, query, Instant.now()));
            }
            return matched;
        }, executor);
    }

    @Override
    public void observeTurn(
            String agentId, String sessionId, String turnId,
            String userInput, String agentOutput) {
        // emit turn observed event — consumers can trigger consolidation
        emit(new MemoryEvent.ConversationTurnObserved(
                agentId, sessionId, turnId, userInput, agentOutput, Instant.now()));
        // auto-consolidate: store the turn as a memory record
        if (userInput != null && !userInput.isBlank()) {
            var record = new MemoryRecord(
                    UUID.randomUUID().toString(), "turn",
                    "turn:" + turnId, userInput + " -> " + (agentOutput == null ? "" : agentOutput),
                    Map.of("turnId", turnId), Instant.now(), 0.7, 3600, sessionId, agentId);
            store(record, "turn_observation");
        }
    }

    @Override
    public Flow.Publisher<MemoryEvent> events() {
        return publisher;
    }

    @Override
    public void addListener(MemoryEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(MemoryEventListener listener) {
        listeners.remove(listener);
    }

    private void emit(MemoryEvent event) {
        publisher.offer(event, null);
        listeners.forEach(l -> {
            try { l.onMemoryEvent(event); }
            catch (Exception ignored) {}
        });
    }

    /** Close the publisher (call on shutdown) */
    public void close() {
        publisher.close();
    }
}
