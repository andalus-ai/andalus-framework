package tech.kayys.wayang.execution.artifact;

import tech.kayys.wayang.execution.sandbox.SandboxId;

import java.time.Instant;
import java.util.Objects;

/**
 * Lineage tracking where an artifact originated and under what execution boundary.
 */
public record ArtifactProvenance(
        String workflowId,
        String taskId,
        String executionId,
        SandboxId sandboxId,
        String agentId,
        Instant timestamp
) {

    public ArtifactProvenance {
        Objects.requireNonNull(sandboxId, "sandboxId cannot be null");
        workflowId = workflowId != null ? workflowId : "";
        taskId = taskId != null ? taskId : "";
        executionId = executionId != null ? executionId : "";
        agentId = agentId != null ? agentId : "";
        timestamp = timestamp != null ? timestamp : Instant.now();
    }
}
