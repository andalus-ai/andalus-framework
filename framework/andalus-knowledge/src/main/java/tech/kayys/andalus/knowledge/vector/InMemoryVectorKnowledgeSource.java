package tech.kayys.andalus.knowledge.vector;

import tech.kayys.andalus.embedding.EmbeddingProvider;
import tech.kayys.andalus.rag.spi.VectorSearchHit;
import tech.kayys.andalus.rag.model.RagChunk;

import java.util.*;
import java.util.concurrent.*;

/**
 * In-memory vector knowledge source for testing and small datasets.
 * Stores documents as float[] vectors and performs exact cosine similarity.
 */
public final class InMemoryVectorKnowledgeSource extends VectorKnowledgeSource {

    private final List<StoredDocument> documents = new CopyOnWriteArrayList<>();

    record StoredDocument(
            String id,
            String content,
            float[] vector,
            Map<String, Object> metadata
    ) {}

    public InMemoryVectorKnowledgeSource(
            String id,
            EmbeddingProvider embeddingProvider,
            String embeddingModel) {
        super(id, embeddingProvider, embeddingModel);
    }

    public void addDocument(String docId, String content, float[] vector, Map<String, Object> metadata) {
        documents.add(new StoredDocument(docId, content, vector, metadata));
    }

    @Override
    protected CompletionStage<List<VectorSearchHit>> vectorSearch(
            float[] queryVector,
            int topK,
            double minScore,
            Map<String, Object> filters) {

        List<VectorSearchHit> hits = documents.stream()
                .map(doc -> {
                    double sim = cosineSimilarity(queryVector, doc.vector());
                    return buildHit(doc, sim);
                })
                .filter(hit -> hit.score() >= minScore)
                .sorted(Comparator.comparingDouble(VectorSearchHit::score).reversed())
                .limit(topK)
                .toList();

        return CompletableFuture.completedFuture(hits);
    }

    private VectorSearchHit buildHit(StoredDocument doc, double score) {
        RagChunk chunk = new RagChunk(doc.id(), doc.id(), 0, doc.content(), doc.metadata());
        return new VectorSearchHit(chunk, score, doc.id());
    }

    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) return 0.0;
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        double denom = Math.sqrt(normA) * Math.sqrt(normB);
        return denom == 0 ? 0 : dot / denom;
    }
}
