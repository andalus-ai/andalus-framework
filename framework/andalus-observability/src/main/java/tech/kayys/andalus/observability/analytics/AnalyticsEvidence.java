package tech.kayys.andalus.observability.analytics;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Supporting evidence reference for an analytics insight.
 */
public record AnalyticsEvidence(
        String sourceType,
        String sourceId,
        Optional<String> eventId,
        Optional<String> metric,
        Optional<String> traceId,
        Optional<Instant> from,
        Optional<Instant> until,
        Map<String, Object> attributes
) {

    public AnalyticsEvidence {
        sourceType = Objects.requireNonNullElse(sourceType, "UNKNOWN");
        sourceId = Objects.requireNonNullElse(sourceId, "UNKNOWN");
        eventId = eventId == null ? Optional.empty() : eventId;
        metric = metric == null ? Optional.empty() : metric;
        traceId = traceId == null ? Optional.empty() : traceId;
        from = from == null ? Optional.empty() : from;
        until = until == null ? Optional.empty() : until;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static AnalyticsEvidence ofMetric(String sourceId, String metricName) {
        return new AnalyticsEvidence("METRIC", sourceId, Optional.empty(), Optional.of(metricName),
                Optional.empty(), Optional.empty(), Optional.empty(), Map.of());
    }

    public static AnalyticsEvidence ofEvent(String sourceId, String eventId) {
        return new AnalyticsEvidence("EVENT", sourceId, Optional.of(eventId), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Map.of());
    }
}
