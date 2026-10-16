package tech.kayys.andalus.observability.analytics;

import java.time.Duration;
import java.util.Map;

/**
 * Derived operational statistics for knowledge retrieval and resolution.
 */
public record KnowledgeStatistics(
        long resolutionCount,
        long successfulResolutions,
        long failedResolutions,
        long partialResolutions,
        long sourceQueries,
        long sourceFailures,
        long sourceDenied,
        long evidenceReceived,
        long evidenceSelected,
        long evidenceExcluded,
        long conflictsDetected,
        Duration averageResolutionLatency,
        Duration p95ResolutionLatency,
        Map<String, KnowledgeSourceStatistics> sources,
        Map<String, Object> diagnostics
) {

    public KnowledgeStatistics {
        averageResolutionLatency = averageResolutionLatency == null ? Duration.ZERO : averageResolutionLatency;
        p95ResolutionLatency = p95ResolutionLatency == null ? Duration.ZERO : p95ResolutionLatency;
        sources = sources == null ? Map.of() : Map.copyOf(sources);
        diagnostics = diagnostics == null ? Map.of() : Map.copyOf(diagnostics);
    }
}
