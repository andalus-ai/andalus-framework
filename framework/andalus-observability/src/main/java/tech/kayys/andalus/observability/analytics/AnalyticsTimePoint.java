package tech.kayys.andalus.observability.analytics;

import java.time.Instant;

/**
 * A discrete point in time for a metric time-series.
 */
public record AnalyticsTimePoint(
        Instant timestamp,
        double value
) {
    public AnalyticsTimePoint {
        timestamp = timestamp == null ? Instant.now() : timestamp;
    }
}
