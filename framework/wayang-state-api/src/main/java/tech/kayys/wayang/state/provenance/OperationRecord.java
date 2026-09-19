package tech.kayys.wayang.state.provenance;

import tech.kayys.wayang.state.artifact.ArtifactReference;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record OperationRecord(
        OperationId id,
        OperationType type,
        String executionId,
        ProvenanceActor actor,
        List<ArtifactReference> inputs,
        List<ArtifactReference> outputs,
        OperationStatus status,
        Instant startedAt,
        Instant completedAt,
        Map<String, Object> metadata
) {
    public OperationRecord {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        inputs = inputs == null ? List.of() : List.copyOf(inputs);
        outputs = outputs == null ? List.of() : List.copyOf(outputs);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
