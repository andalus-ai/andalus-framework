package tech.kayys.andalus.observability.analytics;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * An individual analytical observation record for storage or stream processing.
 */
public record AnalyticsRecord(
        String metric,
        Instant timestamp,
        double value,
        Map<String, String> dimensions
) {

    public AnalyticsRecord {
        metric = Objects.requireNonNullElse(metric, "unknown");
        timestamp = timestamp == null ? Instant.now() : timestamp;
        dimensions = dimensions == null ? Map.of() : Map.copyOf(dimensions);
    }
}
