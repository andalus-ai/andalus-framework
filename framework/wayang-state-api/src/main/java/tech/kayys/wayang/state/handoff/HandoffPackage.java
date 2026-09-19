package tech.kayys.wayang.state.handoff;

import tech.kayys.wayang.state.StateSnapshot;
import tech.kayys.wayang.state.artifact.ArtifactReference;
import tech.kayys.wayang.state.context.ContextSnapshot;
import tech.kayys.wayang.state.provenance.ProvenanceId;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Autonomous agent handoff contract packaging state, context, and artifact references.
 */
public record HandoffPackage(
        HandoffId id,
        String taskId,
        String sourceAgentId,
        String targetAgentId,
        StateSnapshot state,
        ContextSnapshot context,
        List<ArtifactReference> artifacts,
        List<ArtifactReference> evidence,
        ProvenanceId provenanceCursor,
        Instant createdAt,
        Map<String, Object> metadata
) {
    public HandoffPackage {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(taskId, "taskId cannot be null");
        artifacts = artifacts == null ? List.of() : List.copyOf(artifacts);
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
