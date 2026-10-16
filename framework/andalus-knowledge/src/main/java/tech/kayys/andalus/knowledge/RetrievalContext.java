package tech.kayys.andalus.knowledge;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Execution context tailored specifically for a knowledge retrieval operation.
 * Combines query intent, execution/security context, budget, and source constraints.
 */
public record RetrievalContext(
        KnowledgeQuery query,
        KnowledgeContext knowledgeContext,
        KnowledgeBudget budget,
        List<String> sourceIds,
        Optional<Instant> asOf,
        Map<String, Object> attributes
) {

    public RetrievalContext {
        Objects.requireNonNull(query, "query must not be null");
        knowledgeContext = knowledgeContext == null ? KnowledgeContext.empty() : knowledgeContext;
        budget = budget == null ? KnowledgeBudget.defaults() : budget;
        sourceIds = sourceIds == null ? List.of() : List.copyOf(sourceIds);
        asOf = asOf == null ? Optional.empty() : asOf;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static RetrievalContext of(KnowledgeQuery query, KnowledgeContext context) {
        return new RetrievalContext(
                query,
                context,
                KnowledgeBudget.defaults(),
                context != null ? context.sourceIds() : List.of(),
                query != null ? Optional.ofNullable(query.asOf()) : Optional.empty(),
                Map.of()
        );
    }

    public static RetrievalContext of(KnowledgeQuery query, KnowledgeContext context, KnowledgeBudget budget) {
        return new RetrievalContext(
                query,
                context,
                budget,
                context != null ? context.sourceIds() : List.of(),
                query != null ? Optional.ofNullable(query.asOf()) : Optional.empty(),
                Map.of()
        );
    }
}
