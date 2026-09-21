package tech.kayys.andalus.execution.economics;

import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.resource.model.ResourceType;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Historical or real-time record of consumed execution resources.
 */
public record ResourceUsage(
    ExecutionIdentity executionId,
    ResourceType type,
    double unitsConsumed,
    Instant recordedAt,
    Map<String, String> metadata
) {
    public ResourceUsage {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(recordedAt, "recordedAt cannot be null");
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static ResourceUsage of(ExecutionIdentity executionId, ResourceType type, double units) {
        return new ResourceUsage(executionId, type, units, Instant.now(), Map.of());
    }

    public static ResourceUsage of(ExecutionIdentity executionId, ResourceType type, double units, Map<String, String> metadata) {
        return new ResourceUsage(executionId, type, units, Instant.now(), metadata);
    }
}
