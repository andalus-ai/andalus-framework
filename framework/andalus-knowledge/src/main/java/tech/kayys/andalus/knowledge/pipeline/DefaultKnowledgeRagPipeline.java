package tech.kayys.andalus.knowledge.pipeline;

import tech.kayys.andalus.knowledge.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Default implementation of KnowledgeRagPipeline coordinating transformation, multi-query resolution,
 * candidate fusion, re-ranking, and budget-governed evidence selection.
 */
public final class DefaultKnowledgeRagPipeline implements KnowledgeRagPipeline {

    private final KnowledgeResolver resolver;
    private final KnowledgeSelector selector;
    private final RetrievalQueryTransformer transformer;
    private final EvidenceFusionStrategy fusionStrategy;
    private final EvidenceReranker reranker;

    public DefaultKnowledgeRagPipeline(
            KnowledgeResolver resolver,
            KnowledgeSelector selector,
            RetrievalQueryTransformer transformer,
            EvidenceFusionStrategy fusionStrategy,
            EvidenceReranker reranker) {
        this.resolver = Objects.requireNonNull(resolver, "resolver must not be null");
        this.selector = Objects.requireNonNull(selector, "selector must not be null");
        this.transformer = transformer != null ? transformer : RetrievalQueryTransformer.identity();
        this.fusionStrategy = fusionStrategy != null ? fusionStrategy : EvidenceFusionStrategy.deduplicating();
        this.reranker = reranker != null ? reranker : EvidenceReranker.scoreOrdered();
    }

    public DefaultKnowledgeRagPipeline(KnowledgeResolver resolver, KnowledgeSelector selector) {
        this(resolver, selector, null, null, null);
    }

    @Override
    public CompletionStage<KnowledgeRagResult> retrieve(RetrievalContext context) {
        Objects.requireNonNull(context, "context must not be null");
        long startMs = System.currentTimeMillis();

        return transformer.transform(context)
                .thenCompose(queries -> {
                    if (queries == null || queries.isEmpty()) {
                        return CompletableFuture.completedFuture(
                                KnowledgeRagResult.of(context.query(), List.of(),
                                        new KnowledgeSelectionResult(List.of(), List.of(), List.of(), 0, Map.of())));
                    }

                    // Query resolver for all generated queries concurrently
                    List<CompletableFuture<List<KnowledgeEvidence>>> queryFutures = queries.stream()
                            .map(q -> resolver.resolve(q, context.knowledgeContext())
                                    .toCompletableFuture()
                                    .thenApply(KnowledgeResult::evidence))
                            .toList();

                    return CompletableFuture.allOf(queryFutures.toArray(new CompletableFuture[0]))
                            .thenCompose(v -> {
                                List<List<KnowledgeEvidence>> candidateLists = queryFutures.stream()
                                        .map(CompletableFuture::join)
                                        .toList();

                                // Fuse & deduplicate
                                List<KnowledgeEvidence> fused = fusionStrategy.fuse(candidateLists, context);

                                // Rerank
                                return reranker.rerank(fused, context.query(), context)
                                        .thenApply(reranked -> {
                                            // Select under budget
                                            KnowledgeSelectionResult selection = selector.select(
                                                    reranked,
                                                    context.budget(),
                                                    context.knowledgeContext()
                                            );

                                            long duration = System.currentTimeMillis() - startMs;
                                            Map<String, Object> diag = Map.of(
                                                    "queriesGenerated", queries.size(),
                                                    "candidatesFused", fused.size(),
                                                    "selectedCount", selection.selected().size(),
                                                    "durationMs", duration
                                            );

                                            return new KnowledgeRagResult(
                                                    context.query(),
                                                    selection.selected(),
                                                    selection,
                                                    diag
                                            );
                                        });
                            });
                });
    }
}
