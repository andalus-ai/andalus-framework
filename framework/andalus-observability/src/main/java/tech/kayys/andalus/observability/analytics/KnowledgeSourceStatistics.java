package tech.kayys.andalus.observability.analytics;

import java.time.Duration;

/**
 * Operational metrics and statistics for an individual knowledge source.
 */
public record KnowledgeSourceStatistics(
        String sourceId,
        long queryCount,
        long successCount,
        long failureCount,
        long deniedCount,
        long evidenceCount,
        Duration averageLatency,
        Duration p95Latency,
        double partialRate
) {

    public KnowledgeSourceStatistics {
        sourceId = sourceId == null ? "unknown" : sourceId;
        averageLatency = averageLatency == null ? Duration.ZERO : averageLatency;
        p95Latency = p95Latency == null ? Duration.ZERO : p95Latency;
    }
}
