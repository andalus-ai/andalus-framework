package tech.kayys.andalus.memory.reactive;

import tech.kayys.andalus.memory.MemoryRecord;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listens to ConversationTurnObserved events and consolidates memory
 * after a configurable number of turns.
 * Analogous to OpenAI Tasks reactive memory compaction.
 */
public final class TurnDrivenMemoryConsolidator implements MemoryEventListener {

    private final ReactiveMemoryStore store;
    private final MemoryConsolidationPolicy policy;
    private final Map<String, AtomicInteger> turnCounts = new ConcurrentHashMap<>();

    public TurnDrivenMemoryConsolidator(
            ReactiveMemoryStore store,
            MemoryConsolidationPolicy policy) {
        this.store = Objects.requireNonNull(store, "store");
        this.policy = policy != null ? policy : MemoryConsolidationPolicy.afterNTurns(5);
    }

    @Override
    public void onMemoryEvent(MemoryEvent event) {
        if (!(event instanceof MemoryEvent.ConversationTurnObserved turn)) return;

        String key = turn.agentId() + ":" + turn.sessionId();
        int count = turnCounts.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();

        // trigger consolidation based on policy
        store.recall(turn.agentId(), turn.sessionId(), null, 100)
                .thenAccept(candidates -> {
                    List<MemoryRecord> toConsolidate = policy.selectForConsolidation(candidates);
                    if (!toConsolidate.isEmpty()) {
                        consolidate(turn.agentId(), turn.sessionId(), toConsolidate);
                    }
                });
    }

    private void consolidate(String agentId, String sessionId, List<MemoryRecord> records) {
        // Simple: create a summary record
        String summary = records.stream()
                .map(r -> r.key() + ": " + r.value())
                .limit(10)
                .reduce("", (a, b) -> a.isBlank() ? b : a + "\n" + b);

        if (!summary.isBlank()) {
            var consolidated = new MemoryRecord(
                    UUID.randomUUID().toString(), "consolidated_summary",
                    "summary:" + sessionId, summary,
                    Map.of("consolidatedFrom", records.size(), "consolidatedAt", Instant.now().toString()),
                    Instant.now(), 0.8, 86400, sessionId, agentId);
            store.store(consolidated, "consolidation");
        }
    }
}
