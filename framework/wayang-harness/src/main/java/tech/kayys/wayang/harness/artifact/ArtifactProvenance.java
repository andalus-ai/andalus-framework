package tech.kayys.wayang.harness.artifact;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.model.ModelInvocationId;
import tech.kayys.wayang.harness.protocol.TurnId;
import tech.kayys.wayang.harness.tool.ToolInvocationId;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Execution and causal provenance tracking where an artifact originated.
 */
public record ArtifactProvenance(
        ExecutionId executionId,
        Optional<TurnId> turnId,
        Collection<ArtifactId> inputArtifacts,
        Collection<ToolInvocationId> toolInvocations,
        Optional<ModelInvocationId> modelInvocation,
        Optional<String> workspaceSnapshotId
) {
    public ArtifactProvenance {
        Objects.requireNonNull(executionId, "executionId");
        turnId = turnId != null ? turnId : Optional.empty();
        inputArtifacts = inputArtifacts != null ? List.copyOf(inputArtifacts) : List.of();
        toolInvocations = toolInvocations != null ? List.copyOf(toolInvocations) : List.of();
        modelInvocation = modelInvocation != null ? modelInvocation : Optional.empty();
        workspaceSnapshotId = workspaceSnapshotId != null ? workspaceSnapshotId : Optional.empty();
    }

    public static ArtifactProvenance ofExecution(ExecutionId executionId) {
        return new ArtifactProvenance(executionId, Optional.empty(), List.of(), List.of(), Optional.empty(), Optional.empty());
    }
}
