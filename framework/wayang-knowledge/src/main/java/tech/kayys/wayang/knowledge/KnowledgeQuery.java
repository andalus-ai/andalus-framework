package tech.kayys.wayang.knowledge;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Domain-neutral request for knowledge retrieval.
 *
 * @param text semantic query text
 * @param topK maximum number of results to return
 * @param minScore minimum acceptable relevance score
 * @param scope logical knowledge scope
 * @param asOf point-in-time constraint for validity
 * @param filters structured retrieval filters
 * @param metadata request metadata for tracing or policy
 */
public record KnowledgeQuery(
        String text,
        int topK,
        double minScore,
        String scope,
        Instant asOf,
        Map<String, Object> filters,
        Map<String, Object> metadata
) {

    public KnowledgeQuery {
        text = Objects.requireNonNullElse(text, "").trim();

        if (topK < 1) {
            topK = 5;
        }

        if (minScore < 0.0) {
            minScore = 0.0;
        }

        scope = scope == null || scope.isBlank() ? "default" : scope;
        filters = filters == null ? Map.of() : Map.copyOf(filters);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    /**
     * Creates a query with default retrieval options.
     *
     * @param text semantic query text
     * @return a normalized query
     */
    public static KnowledgeQuery of(String text) {
        return new KnowledgeQuery(text, 5, 0.0, "default", null, Map.of(), Map.of());
    }

    /**
     * Returns a copy with a different result limit.
     *
     * @param value requested maximum result count
     * @return a query with the new limit
     */
    public KnowledgeQuery withTopK(int value) {
        return new KnowledgeQuery(text, value, minScore, scope, asOf, filters, metadata);
    }

    /**
     * Returns a copy constrained to a different scope.
     *
     * @param value logical knowledge scope
     * @return a query with the new scope
     */
    public KnowledgeQuery withScope(String value) {
        return new KnowledgeQuery(text, topK, minScore, value, asOf, filters, metadata);
    }

    /**
     * Returns a copy evaluated at a point in time.
     *
     * @param value point-in-time validity constraint
     * @return a query with the new temporal constraint
     */
    public KnowledgeQuery asOf(Instant value) {
        return new KnowledgeQuery(text, topK, minScore, scope, value, filters, metadata);
    }

    /**
     * Returns a copy with a different relevance threshold.
     *
     * @param score minimum relevance score
     * @return a query with the new threshold
     */
    public KnowledgeQuery withMinScore(double score) {
        return new KnowledgeQuery(text, topK, score, scope, asOf, filters, metadata);
    }
}
