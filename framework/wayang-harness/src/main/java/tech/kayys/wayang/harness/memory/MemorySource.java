package tech.kayys.wayang.harness.memory;

import java.time.Instant;
import java.util.Objects;

public record MemorySource(
        String agentId,
        String executionId,
        String sourceType,
        Instant observedAt
) {
    public MemorySource {
        agentId = agentId == null ? "unknown" : agentId;
        executionId = executionId == null ? "unknown" : executionId;
        sourceType = sourceType == null ? "observation" : sourceType;
        observedAt = observedAt == null ? Instant.now() : observedAt;
    }

    public static MemorySource of(String agentId, String executionId) {
        return new MemorySource(agentId, executionId, "observation", Instant.now());
    }
}
