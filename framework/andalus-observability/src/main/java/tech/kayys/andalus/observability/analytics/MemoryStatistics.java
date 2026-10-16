package tech.kayys.andalus.observability.analytics;

import java.time.Duration;
import java.util.Map;

/**
 * Derived operational statistics for memory retrieval, admission, and lifecycle.
 */
public record MemoryStatistics(
        long retrievalCount,
        long retrievalHitCount,
        long retrievalEmptyCount,
        long retrievalDeniedCount,
        long candidateCount,
        long admittedCount,
        long rejectedCount,
        long activeCount,
        long expiredCount,
        long archivedCount,
        long deletedCount,
        long conflictsDetected,
        long conflictsResolved,
        Duration averageRetrievalLatency,
        Duration p95RetrievalLatency,
        Map<String, Long> rejectionReasons,
        Map<String, Object> diagnostics
) {

    public MemoryStatistics {
        averageRetrievalLatency = averageRetrievalLatency == null ? Duration.ZERO : averageRetrievalLatency;
        p95RetrievalLatency = p95RetrievalLatency == null ? Duration.ZERO : p95RetrievalLatency;
        rejectionReasons = rejectionReasons == null ? Map.of() : Map.copyOf(rejectionReasons);
        diagnostics = diagnostics == null ? Map.of() : Map.copyOf(diagnostics);
    }
}
