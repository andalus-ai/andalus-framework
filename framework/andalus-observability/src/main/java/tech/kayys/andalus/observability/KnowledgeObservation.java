package tech.kayys.andalus.observability;

import java.util.Map;
import java.util.Objects;

/**
 * Domain-neutral telemetry payload representing an observation of a knowledge operation.
 *
 * <p>Captures operational counters, latencies, and metadata without leaking full raw evidence/content.</p>
 */
public record KnowledgeObservation(
        String operationId,
        String executionId,
        String traceId,
        String agentId,
        String sourceId,
        String queryId,
        String event,
        long candidateCount,
        long selectedCount,
        long excludedCount,
        long durationMillis,
        boolean partial,
        Map<String, Object> attributes
) {

    public KnowledgeObservation {
        operationId = operationId == null ? "" : operationId;
        executionId = executionId == null ? "" : executionId;
        traceId = traceId == null ? "" : traceId;
        agentId = agentId == null ? "" : agentId;
        sourceId = sourceId == null ? "" : sourceId;
        queryId = queryId == null ? "" : queryId;
        event = Objects.requireNonNullElse(event, "UNKNOWN");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static KnowledgeObservation of(String event, String executionId, String traceId) {
        return new KnowledgeObservation(null, executionId, traceId, null, null, null, event, 0, 0, 0, 0, false, Map.of());
    }
}
