package tech.kayys.andalus.telemetry;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.time.Instant;
import java.util.Map;

/**
 * Metric
 */
public record Metric(
    String name,
    double value,
    MetricType type,
    Map<String, String> tags,
    Instant timestamp
) {}
