package tech.kayys.andalus.knowledge.pipeline;

import tech.kayys.andalus.knowledge.KnowledgeEvidence;
import tech.kayys.andalus.knowledge.KnowledgeQuery;
import tech.kayys.andalus.knowledge.RetrievalContext;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Re-scores and re-ranks retrieved evidence candidates prior to final budget selection.
 */
public interface EvidenceReranker {

    CompletionStage<List<KnowledgeEvidence>> rerank(
            List<KnowledgeEvidence> candidates,
            KnowledgeQuery query,
            RetrievalContext context
    );

    static EvidenceReranker scoreOrdered() {
        return (candidates, query, context) -> {
            if (candidates == null) {
                return CompletableFuture.completedFuture(List.of());
            }
            List<KnowledgeEvidence> sorted = candidates.stream()
                    .sorted(Comparator.comparingDouble(KnowledgeEvidence::score).reversed())
                    .toList();
            return CompletableFuture.completedFuture(sorted);
        };
    }
}
