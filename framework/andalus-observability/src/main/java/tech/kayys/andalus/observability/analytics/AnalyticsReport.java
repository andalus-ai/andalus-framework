package tech.kayys.andalus.observability.analytics;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Consolidated operational analytics report.
 */
public record AnalyticsReport(
        AnalyticsQuery query,
        Optional<KnowledgeStatistics> knowledge,
        Optional<MemoryStatistics> memory,
        Optional<KnowledgeMemoryPromotionStatistics> promotion,
        List<AnalyticsSeries> series,
        List<AnalyticsInsight> insights,
        Map<String, Object> diagnostics
) {

    public AnalyticsReport {
        Objects.requireNonNull(query, "query must not be null");
        knowledge = knowledge == null ? Optional.empty() : knowledge;
        memory = memory == null ? Optional.empty() : memory;
        promotion = promotion == null ? Optional.empty() : promotion;
        series = series == null ? List.of() : List.copyOf(series);
        insights = insights == null ? List.of() : List.copyOf(insights);
        diagnostics = diagnostics == null ? Map.of() : Map.copyOf(diagnostics);
    }

    public static AnalyticsReport empty(AnalyticsQuery query) {
        return new AnalyticsReport(query, Optional.empty(), Optional.empty(), Optional.empty(),
                List.of(), List.of(), Map.of());
    }
}
