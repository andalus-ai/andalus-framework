package tech.kayys.andalus.knowledge;

/**
 * Factory for assembling ephemeral RetrievalContext instances for retrieval execution.
 */
public interface RetrievalContextFactory {

    RetrievalContext create(KnowledgeQuery query, KnowledgeContext context);
}
