package tech.kayys.andalus.knowledge.context;

import tech.kayys.andalus.knowledge.KnowledgeBudget;
import tech.kayys.andalus.knowledge.KnowledgeInvocation;
import tech.kayys.andalus.knowledge.KnowledgeQuery;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Execution context tailored specifically for a knowledge retrieval operation.
 * Isolates query intent, temporal point, budget, and source constraints
 * from the broader agent or tenant context.
 */
public record RetrievalContext(
        String queryText,
        String scope,
        Instant asOf,
        KnowledgeBudget budget,
        List<String> sourceConstraints,
        Map<String, Object> filters,
        Optional<KnowledgeInvocation> invocation,
        Map<String, Object> attributes
) {

    public RetrievalContext {
        Objects.requireNonNull(queryText, "queryText must not be null");
        scope = scope == null || scope.isBlank() ? "default" : scope;
        asOf = asOf == null ? Instant.now() : asOf;
        budget = budget == null ? KnowledgeBudget.defaults() : budget;
        sourceConstraints = sourceConstraints == null ? List.of() : List.copyOf(sourceConstraints);
        filters = filters == null ? Map.of() : Map.copyOf(filters);
        invocation = invocation == null ? Optional.empty() : invocation;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static RetrievalContext of(String queryText) {
        return new RetrievalContext(
                queryText,
                "default",
                Instant.now(),
                KnowledgeBudget.defaults(),
                List.of(),
                Map.of(),
                Optional.empty(),
                Map.of()
        );
    }

    public RetrievalContext withScope(String newScope) {
        return new RetrievalContext(queryText, newScope, asOf, budget, sourceConstraints, filters, invocation, attributes);
    }

    public RetrievalContext withBudget(KnowledgeBudget newBudget) {
        return new RetrievalContext(queryText, scope, asOf, newBudget, sourceConstraints, filters, invocation, attributes);
    }

    public RetrievalContext withAsOf(Instant newAsOf) {
        return new RetrievalContext(queryText, scope, newAsOf, budget, sourceConstraints, filters, invocation, attributes);
    }

    public RetrievalContext withSources(List<String> sources) {
        return new RetrievalContext(queryText, scope, asOf, budget, sources, filters, invocation, attributes);
    }

    public RetrievalContext withInvocation(KnowledgeInvocation inv) {
        return new RetrievalContext(queryText, scope, asOf, budget, sourceConstraints, filters, Optional.ofNullable(inv), attributes);
    }

    /**
     * Converts this retrieval context into a canonical KnowledgeQuery.
     */
    public KnowledgeQuery toKnowledgeQuery() {
        return new KnowledgeQuery(
                queryText,
                budget.maxItems(),
                budget.minimumScore(),
                scope,
                asOf,
                filters,
                attributes
        );
    }
}
