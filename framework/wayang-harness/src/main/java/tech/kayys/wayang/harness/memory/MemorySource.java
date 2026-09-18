package tech.kayys.wayang.harness.memory;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a memory source.
 *
 * <p>Its components capture `agent id`, `execution id`, `source type`, `observed at`.</p>
 *
 * @param agentId the agent id
 * @param executionId the execution id
 * @param sourceType the source type
 * @param observedAt the observed at
 */


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
