package tech.kayys.andalus.knowledge;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of RetrievalContextFactory.
 */
public final class DefaultRetrievalContextFactory implements RetrievalContextFactory {

    private final KnowledgeBudgetProvider budgetProvider;

    public DefaultRetrievalContextFactory(KnowledgeBudgetProvider budgetProvider) {
        this.budgetProvider = budgetProvider != null ? budgetProvider : KnowledgeBudgetProvider.defaults();
    }

    public DefaultRetrievalContextFactory() {
        this(KnowledgeBudgetProvider.defaults());
    }

    @Override
    public RetrievalContext create(KnowledgeQuery query, KnowledgeContext context) {
        Objects.requireNonNull(query, "query must not be null");
        KnowledgeContext effectiveContext = context == null ? KnowledgeContext.empty() : context;
        KnowledgeBudget budget = budgetProvider.budgetFor(KnowledgeRuntimeContext.of(effectiveContext.agentId(), ""));

        return new RetrievalContext(
                query,
                effectiveContext,
                budget,
                effectiveContext.sourceIds(),
                Optional.ofNullable(query.asOf()),
                Map.of()
        );
    }
}
