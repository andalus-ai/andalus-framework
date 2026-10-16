package tech.kayys.andalus.knowledge.vector;

import tech.kayys.andalus.knowledge.KnowledgeQuery;
import java.util.Map;

/**
 * Ergonomic builder for semantic/vector search queries.
 * Provides a fluent API similar to LangChain's similarity_search().
 */
public final class SemanticKnowledgeQuery {

    private SemanticKnowledgeQuery() {}

    /** Simple semantic search: `SemanticKnowledgeQuery.find("what is GDP")` */
    public static KnowledgeQuery find(String text) {
        return KnowledgeQuery.of(text);
    }

    /** With explicit result limit */
    public static KnowledgeQuery find(String text, int topK) {
        return KnowledgeQuery.of(text).withTopK(topK);
    }

    /** With minimum similarity threshold */
    public static KnowledgeQuery findAbove(String text, double minScore) {
        return KnowledgeQuery.of(text).withMinScore(minScore);
    }

    /** Scoped to a specific knowledge domain */
    public static KnowledgeQuery findInScope(String text, String scope) {
        return KnowledgeQuery.of(text).withScope(scope);
    }

    /** Full control */
    public static KnowledgeQuery build(String text, int topK, double minScore, String scope) {
        return KnowledgeQuery.of(text).withTopK(topK).withMinScore(minScore).withScope(scope);
    }
}
