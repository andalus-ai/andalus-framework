package tech.kayys.andalus.knowledge.pipeline;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.knowledge.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class KnowledgeRagPipelineTest {

    static KnowledgeEvidence ev(String id, double score) {
        var item = new KnowledgeItem(id, "src-1", "fact", "title-" + id, "content-" + id,
                Map.of(), null, null, KnowledgeValidity.active());
        return KnowledgeEvidence.of(item, score, "hybrid");
    }

    @Test
    void queryTransformerIdentityProducesOriginalQuery() throws Exception {
        var transformer = RetrievalQueryTransformer.identity();
        var q = KnowledgeQuery.of("economic policy");
        var ctx = RetrievalContext.of(q, KnowledgeContext.empty());

        List<KnowledgeQuery> queries = transformer.transform(ctx).toCompletableFuture().get(5, TimeUnit.SECONDS);
        assertEquals(1, queries.size());
        assertEquals("economic policy", queries.get(0).text());
    }

    @Test
    void fusionStrategyDeduplicatesIdenticalItemIds() {
        var fusion = EvidenceFusionStrategy.deduplicating();
        var listA = List.of(ev("doc-1", 0.9), ev("doc-2", 0.8));
        var listB = List.of(ev("doc-2", 0.85), ev("doc-3", 0.7));

        var fused = fusion.fuse(List.of(listA, listB), RetrievalContext.of(KnowledgeQuery.of("test"), KnowledgeContext.empty()));
        assertEquals(3, fused.size());
        assertEquals("doc-1", fused.get(0).item().id());
        assertEquals("doc-2", fused.get(1).item().id());
        assertEquals("doc-3", fused.get(2).item().id());
    }

    @Test
    void rerankerOrdersByScoreDescending() throws Exception {
        var reranker = EvidenceReranker.scoreOrdered();
        var list = List.of(ev("doc-low", 0.3), ev("doc-high", 0.95), ev("doc-mid", 0.7));

        var reranked = reranker.rerank(list, KnowledgeQuery.of("test"), RetrievalContext.of(KnowledgeQuery.of("test"), KnowledgeContext.empty()))
                .toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertEquals(3, reranked.size());
        assertEquals("doc-high", reranked.get(0).item().id());
        assertEquals("doc-mid", reranked.get(1).item().id());
        assertEquals("doc-low", reranked.get(2).item().id());
    }

    @Test
    void pipelineCoordinatesRetrievalAndSelection() throws Exception {
        KnowledgeResolver resolver = (q, c) -> CompletableFuture.completedFuture(
                KnowledgeResult.of(q, List.of(ev("doc-1", 0.9), ev("doc-2", 0.8)))
        );

        KnowledgeSelector selector = (candidates, budget, c) ->
                new KnowledgeSelectionResult(List.of(candidates.get(0)), List.of(), List.of(), 100, Map.of());

        var pipeline = new DefaultKnowledgeRagPipeline(resolver, selector);
        var q = KnowledgeQuery.of("test query");
        var retCtx = RetrievalContext.of(q, KnowledgeContext.empty(), KnowledgeBudget.defaults());

        KnowledgeRagResult result = pipeline.retrieve(retCtx).toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertNotNull(result);
        assertEquals(1, result.evidence().size());
        assertEquals("doc-1", result.evidence().get(0).item().id());
        assertTrue(result.diagnostics().containsKey("candidatesFused"));
        assertTrue(result.diagnostics().containsKey("selectedCount"));
    }
}
