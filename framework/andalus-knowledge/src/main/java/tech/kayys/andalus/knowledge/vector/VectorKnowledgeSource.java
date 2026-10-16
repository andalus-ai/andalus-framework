package tech.kayys.andalus.knowledge.vector;

import tech.kayys.andalus.embedding.EmbeddingProvider;
import tech.kayys.andalus.knowledge.*;
import tech.kayys.andalus.rag.spi.VectorSearchHit;
import tech.kayys.andalus.rag.model.RagChunk;

import java.util.*;
import java.util.concurrent.*;

/**
 * Abstract base for vector-backed KnowledgeSource implementations.
 * Subclasses provide the vector search backend; this class handles
 * embedding, scoring, and KnowledgeEvidence construction.
 *
 * Usage is analogous to LangChain's VectorStoreRetriever but follows
 * Andalus's KnowledgeSource SPI — no separate Retriever abstraction needed.
 */
public abstract class VectorKnowledgeSource implements KnowledgeSource {

    private final String id;
    private final EmbeddingProvider embeddingProvider;
    private final String embeddingModel;

    protected VectorKnowledgeSource(
            String id,
            EmbeddingProvider embeddingProvider,
            String embeddingModel) {
        this.id = Objects.requireNonNull(id, "id");
        this.embeddingProvider = Objects.requireNonNull(embeddingProvider, "embeddingProvider");
        this.embeddingModel = embeddingModel != null ? embeddingModel : "default";
    }

    @Override
    public final String id() { return id; }

    @Override
    public boolean isHealthy() { return true; }

    /**
     * Perform vector similarity search.
     * @param queryVector the embedded query vector
     * @param topK max results
     * @param minScore minimum similarity score
     * @param filters optional metadata filters
     * @return list of search hits
     */
    protected abstract CompletionStage<List<VectorSearchHit>> vectorSearch(
            float[] queryVector,
            int topK,
            double minScore,
            Map<String, Object> filters);

    /**
     * Convert a VectorSearchHit to a KnowledgeItem.
     * Subclasses override to customize field mapping.
     */
    protected KnowledgeItem toKnowledgeItem(VectorSearchHit hit) {
        RagChunk payload = hit.payload();
        String content = payload != null ? payload.text() : "";
        Map<String, Object> metadata = payload != null ? payload.metadata() : Map.of();
        String title = hit.id();

        return new KnowledgeItem(
                hit.id(),
                id(),
                "document",
                title,
                content,
                metadata,
                null, // provenance
                null, // authority
                KnowledgeValidity.active()
        );
    }

    @Override
    public final CompletionStage<KnowledgeResult> query(
            KnowledgeQuery query, KnowledgeContext context) {

        return CompletableFuture.supplyAsync(() -> {
            // embed the query text
            List<float[]> vectors = embeddingProvider.embedAll(
                    List.of(query.text()), embeddingModel);
            if (vectors.isEmpty()) {
                return null;
            }
            return vectors.get(0);
        }).thenCompose(queryVector -> {
            if (queryVector == null) {
                return CompletableFuture.completedFuture(KnowledgeResult.of(query, List.of()));
            }
            return vectorSearch(queryVector, query.topK(), query.minScore(), query.filters())
                .thenApply(hits -> {
                    List<KnowledgeEvidence> evidence = hits.stream()
                            .map(hit -> KnowledgeEvidence.of(
                                    toKnowledgeItem(hit),
                                    hit.score(),
                                    "vector-similarity"))
                            .toList();
                    return KnowledgeResult.of(query, evidence);
                });
        });
    }
}
