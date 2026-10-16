package tech.kayys.andalus.observability;

import java.time.Duration;
import java.util.Map;

public record ExecutionMetrics(
        String executionId,
        String traceId,
        Duration duration,
        long turns,
        long modelCalls,
        long toolCalls,
        long memoryCalls,
        long knowledgeCalls,
        long inputTokens,
        long outputTokens,
        long totalTokens,
        double estimatedCost,
        Map<String, Object> resources
) {

    public ExecutionMetrics {
        duration = duration == null
                ? Duration.ZERO
                : duration;

        resources = resources == null
                ? Map.of()
                : Map.copyOf(resources);
    }
}
