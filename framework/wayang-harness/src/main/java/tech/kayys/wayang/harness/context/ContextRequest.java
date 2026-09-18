package tech.kayys.wayang.harness.context;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a context request.
 *
 * <p>Its components capture `query`, `max tokens`, `kinds`, `parameters`.</p>
 *
 * @param query the query
 * @param maxTokens the max tokens
 * @param kinds the kinds
 * @param parameters the parameters
 */


public record ContextRequest(
        String query,
        long maxTokens,
        Set<ContextKind> kinds,
        Map<String, Object> parameters
) {
    public ContextRequest {
        query = query == null ? "" : query;
        maxTokens = maxTokens <= 0 ? 8192L : maxTokens;
        kinds = kinds == null ? Set.of() : Set.copyOf(kinds);
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }

    public static ContextRequest of(String query, long maxTokens) {
        return new ContextRequest(query, maxTokens, Set.of(), Map.of());
    }
}
