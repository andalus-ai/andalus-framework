package tech.kayys.andalus.harness.environment.v3.resource;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * Metric measurement of consumed resources by an execution.
 */
public record ResourceUsage(
        ExecutionId executionId,
        Duration cpuTime,
        long memoryPeakBytes,
        long diskBytes,
        long networkBytesTransferred,
        Map<String, Long> customMetrics
) {
    public ResourceUsage {
        Objects.requireNonNull(executionId, "executionId");
        cpuTime = cpuTime != null ? cpuTime : Duration.ZERO;
        customMetrics = customMetrics != null ? Map.copyOf(customMetrics) : Map.of();
    }

    public static ResourceUsage zero(ExecutionId executionId) {
        return new ResourceUsage(executionId, Duration.ZERO, 0L, 0L, 0L, Map.of());
    }
}
