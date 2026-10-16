package tech.kayys.andalus.observability;

import java.util.Map;
import java.util.Objects;

/**
 * Domain-neutral telemetry payload representing an observation of a memory operation.
 *
 * <p>Captures operational counters, latencies, and lifecycle state changes without leaking raw memory content.</p>
 */
public record MemoryObservation(
        String operationId,
        String executionId,
        String traceId,
        String agentId,
        String memoryId,
        String memoryType,
        String memoryScope,
        String event,
        boolean admitted,
        String rejectionReason,
        long durationMillis,
        Map<String, Object> attributes
) {

    public MemoryObservation {
        operationId = operationId == null ? "" : operationId;
        executionId = executionId == null ? "" : executionId;
        traceId = traceId == null ? "" : traceId;
        agentId = agentId == null ? "" : agentId;
        memoryId = memoryId == null ? "" : memoryId;
        memoryType = memoryType == null ? "UNKNOWN" : memoryType;
        memoryScope = memoryScope == null ? "UNKNOWN" : memoryScope;
        event = Objects.requireNonNullElse(event, "UNKNOWN");
        rejectionReason = rejectionReason == null ? "" : rejectionReason;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static MemoryObservation of(String event, String executionId, String traceId, String memoryId) {
        return new MemoryObservation(null, executionId, traceId, null, memoryId, "UNKNOWN", "UNKNOWN", event, true, "", 0, Map.of());
    }
}
