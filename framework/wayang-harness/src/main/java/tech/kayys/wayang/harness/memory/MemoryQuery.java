package tech.kayys.wayang.harness.memory;

import java.util.Map;
import java.util.Objects;

public record MemoryQuery(
        String query,
        MemoryScope scope,
        int limit,
        Map<String, Object> filters
) {
    public MemoryQuery {
        query = query == null ? "" : query;
        scope = scope == null ? MemoryScope.AGENT : scope;
        limit = limit <= 0 ? 10 : limit;
        filters = filters == null ? Map.of() : Map.copyOf(filters);
    }

    public static MemoryQuery of(String query, MemoryScope scope) {
        return new MemoryQuery(query, scope, 10, Map.of());
    }

    public static MemoryQuery of(String query, MemoryScope scope, int limit) {
        return new MemoryQuery(query, scope, limit, Map.of());
    }
}
