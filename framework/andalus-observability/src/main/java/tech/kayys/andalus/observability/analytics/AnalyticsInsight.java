package tech.kayys.andalus.observability.analytics;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * An evidence-backed operational insight derived from observations, metrics, or traces.
 */
public record AnalyticsInsight(
        String id,
        String category,
        String title,
        String description,
        Instant generatedAt,
        AnalyticsSeverity severity,
        List<AnalyticsEvidence> evidence,
        Map<String, Object> attributes
) {

    public AnalyticsInsight {
        id = id == null || id.isBlank() ? "insight-" + UUID.randomUUID() : id;
        category = Objects.requireNonNullElse(category, "GENERAL");
        title = Objects.requireNonNullElse(title, "");
        description = Objects.requireNonNullElse(description, "");
        generatedAt = generatedAt == null ? Instant.now() : generatedAt;
        severity = severity == null ? AnalyticsSeverity.INFO : severity;
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static AnalyticsInsight info(String category, String title, String description) {
        return new AnalyticsInsight(null, category, title, description, Instant.now(),
                AnalyticsSeverity.INFO, List.of(), Map.of());
    }

    public static AnalyticsInsight warning(String category, String title, String description, List<AnalyticsEvidence> evidence) {
        return new AnalyticsInsight(null, category, title, description, Instant.now(),
                AnalyticsSeverity.WARNING, evidence, Map.of());
    }
}
