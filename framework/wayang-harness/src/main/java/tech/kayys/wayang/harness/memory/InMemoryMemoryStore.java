package tech.kayys.wayang.harness.memory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Provides in memory memory store behavior for the Wayang framework.
 */


public class InMemoryMemoryStore implements MemoryStore {

    private final Map<MemoryId, MemoryEntry> entries = new ConcurrentHashMap<>();

    @Override
    public MemoryId store(MemoryEntry entry) {
        Objects.requireNonNull(entry, "entry");
        entries.put(entry.id(), entry);
        return entry.id();
    }

    @Override
    public Optional<MemoryEntry> get(MemoryId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(entries.get(id));
    }

    @Override
    public void delete(MemoryId id) {
        Objects.requireNonNull(id, "id");
        entries.remove(id);
    }

    @Override
    public MemoryQueryResult query(MemoryQuery query) {
        Objects.requireNonNull(query, "query");
        String q = query.query().toLowerCase();

        List<MemoryEntry> matched = entries.values().stream()
                .filter(e -> query.scope() == null || e.scope() == query.scope() || query.scope() == MemoryScope.GLOBAL)
                .filter(e -> q.isBlank() || e.content().toLowerCase().contains(q))
                .limit(query.limit())
                .collect(Collectors.toList());

        return new MemoryQueryResult(matched, Map.of("totalMatched", matched.size()));
    }
}
