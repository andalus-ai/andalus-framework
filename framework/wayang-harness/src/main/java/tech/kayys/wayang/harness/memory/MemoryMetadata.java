package tech.kayys.wayang.harness.memory;

import java.time.Instant;
import java.util.Map;

public record MemoryMetadata(
        Instant createdAt,
        Instant updatedAt,
        MemorySource source,
        double confidence,
        Map<String, Object> attributes
) {
    public MemoryMetadata {
        createdAt = createdAt == null ? Instant.now() : createdAt;
        updatedAt = updatedAt == null ? createdAt : updatedAt;
        source = source == null ? MemorySource.of("system", "init") : source;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static MemoryMetadata observed(String agentId, String executionId, double confidence) {
        return new MemoryMetadata(Instant.now(), Instant.now(), MemorySource.of(agentId, executionId), confidence, Map.of());
    }
}
