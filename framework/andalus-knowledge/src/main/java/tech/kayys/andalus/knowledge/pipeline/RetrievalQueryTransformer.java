package tech.kayys.andalus.knowledge.pipeline;

import tech.kayys.andalus.knowledge.KnowledgeQuery;
import tech.kayys.andalus.knowledge.RetrievalContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Transforms a single retrieval request into one or more targeted knowledge queries (e.g. expansion, decomposition).
 */
public interface RetrievalQueryTransformer {

    CompletionStage<List<KnowledgeQuery>> transform(RetrievalContext context);

    static RetrievalQueryTransformer identity() {
        return context -> CompletableFuture.completedFuture(
                List.of(context.query())
        );
    }
}
