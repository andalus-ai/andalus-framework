package tech.kayys.andalus.execution.mobility;

import java.time.Instant;
import java.util.Objects;

/**
 * Durable execution identity that survives runtime migration.
 */
public record ExecutionIdentity(
        String id,
        String workflowId,
        String agentId,
        Instant createdAt
) {
    public ExecutionIdentity {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(workflowId, "workflowId cannot be null");
        Objects.requireNonNull(agentId, "agentId cannot be null");
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public static ExecutionIdentity of(String id, String workflowId, String agentId) {
        return new ExecutionIdentity(id, workflowId, agentId, Instant.now());
    }
}
