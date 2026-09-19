package tech.kayys.wayang.state.checkpoint;

import tech.kayys.wayang.state.StateSnapshot;
import tech.kayys.wayang.state.artifact.ArtifactReference;
import tech.kayys.wayang.state.context.ContextSnapshot;
import tech.kayys.wayang.state.provenance.ProvenanceId;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * High-level unified checkpoint for execution boundary recovery.
 */
public record Checkpoint(
        CheckpointId id,
        String executionId,
        StateSnapshot executionState,
        ContextSnapshot contextSnapshot,
        List<ArtifactReference> artifacts,
        ProvenanceId provenanceOffset,
        Instant timestamp,
        Map<String, Object> metadata
) {
    public Checkpoint {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        artifacts = artifacts == null ? List.of() : List.copyOf(artifacts);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
