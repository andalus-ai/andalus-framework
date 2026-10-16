package tech.kayys.andalus.observability.analytics;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A named time series of analytical observations.
 */
public record AnalyticsSeries(
        String metric,
        String unit,
        List<AnalyticsTimePoint> points,
        Map<String, String> dimensions
) {

    public AnalyticsSeries {
        metric = Objects.requireNonNullElse(metric, "unknown");
        unit = unit == null ? "" : unit;
        points = points == null ? List.of() : List.copyOf(points);
        dimensions = dimensions == null ? Map.of() : Map.copyOf(dimensions);
    }
}
