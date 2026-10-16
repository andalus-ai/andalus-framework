package tech.kayys.andalus.knowledge.vector;

import java.util.Map;
import java.util.Objects;

/**
 * Configuration for a vector-backed knowledge source.
 * Keeps connection details separate from the source implementation.
 */
public record VectorKnowledgeSourceConfig(
        String sourceId,
        String embeddingModel,
        String embeddingProvider,
        int defaultTopK,
        double defaultMinScore,
        Map<String, Object> backendConfig
) {
    public VectorKnowledgeSourceConfig {
        Objects.requireNonNull(sourceId, "sourceId");
        embeddingModel = embeddingModel == null ? "default" : embeddingModel;
        defaultTopK = defaultTopK < 1 ? 5 : defaultTopK;
        defaultMinScore = Math.max(0.0, Math.min(1.0, defaultMinScore));
        backendConfig = backendConfig == null ? Map.of() : Map.copyOf(backendConfig);
    }

    public static VectorKnowledgeSourceConfig defaults(String sourceId) {
        return new VectorKnowledgeSourceConfig(sourceId, "default", null, 5, 0.2, Map.of());
    }
}
