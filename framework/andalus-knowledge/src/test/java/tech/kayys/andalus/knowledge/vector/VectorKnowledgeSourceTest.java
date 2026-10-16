package tech.kayys.andalus.knowledge.vector;

import tech.kayys.andalus.embedding.EmbeddingProvider;
import tech.kayys.andalus.knowledge.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class VectorKnowledgeSourceTest {

    // Deterministic embedding provider: hashes text to fixed-dimension vector
    static EmbeddingProvider deterministicProvider() {
        return new EmbeddingProvider() {
            @Override public String name() { return "test"; }
            @Override public boolean supports(String model) { return true; }
            @Override public List<float[]> embedAll(List<String> inputs, String model) {
                return inputs.stream().map(t -> {
                    float[] v = new float[4];
                    for (int i = 0; i < 4; i++) v[i] = (t.hashCode() >> (i * 8)) & 0xFF;
                    // normalize
                    float norm = 0;
                    for (float f : v) norm += f * f;
                    norm = (float) Math.sqrt(norm);
                    if (norm > 0) for (int i = 0; i < 4; i++) v[i] /= norm;
                    return v;
                }).toList();
            }
        };
    }

    @Test void inMemorySourceQueryReturnsEvidence() throws Exception {
        var provider = deterministicProvider();
        var source = new InMemoryVectorKnowledgeSource("test-vec", provider, "default");

        // pre-compute vector for "hello world" using our provider
        float[] docVec = provider.embedAll(List.of("hello world"), "default").get(0);
        source.addDocument("doc1", "hello world content", docVec, Map.of());

        var query = SemanticKnowledgeQuery.find("hello world", 5);
        var result = source.query(query, KnowledgeContext.empty()).toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertNotNull(result);
        assertFalse(result.evidence().isEmpty());
        assertEquals("test-vec", result.evidence().get(0).item().sourceId());
    }

    @Test void semanticQueryBuilderCreatesKnowledgeQuery() {
        var q = SemanticKnowledgeQuery.find("capital of Indonesia", 10);
        assertEquals("capital of Indonesia", q.text());
        assertEquals(10, q.topK());

        var q2 = SemanticKnowledgeQuery.findAbove("test", 0.7);
        assertEquals(0.7, q2.minScore(), 0.001);

        var q3 = SemanticKnowledgeQuery.findInScope("test", "policy");
        assertEquals("policy", q3.scope());
    }

    @Test void configDefaults() {
        var cfg = VectorKnowledgeSourceConfig.defaults("src1");
        assertEquals("src1", cfg.sourceId());
        assertTrue(cfg.defaultTopK() > 0);
        assertTrue(cfg.defaultMinScore() >= 0.0);
    }

    @Test void vectorSourceImplementsKnowledgeSource() {
        var provider = deterministicProvider();
        var source = new InMemoryVectorKnowledgeSource("v1", provider, "default");
        // must satisfy KnowledgeSource SPI
        KnowledgeSource ks = source;
        assertEquals("v1", ks.id());
        assertTrue(ks.isHealthy());
    }

    @Test void cosineSimilarityOfIdenticalVectorsIsOne() throws Exception {
        var provider = deterministicProvider();
        var source = new InMemoryVectorKnowledgeSource("sim-test", provider, "default");
        float[] vec = provider.embedAll(List.of("exact match text"), "default").get(0);
        source.addDocument("d1", "exact match text", vec, Map.of());

        var q = SemanticKnowledgeQuery.findAbove("exact match text", 0.0);
        var result = source.query(q, KnowledgeContext.empty()).toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertFalse(result.evidence().isEmpty());
        // score should be ~1.0 for identical vectors
        double score = result.evidence().get(0).score();
        assertTrue(score > 0.99, "expected cosine ~1.0, got " + score);
    }
}
