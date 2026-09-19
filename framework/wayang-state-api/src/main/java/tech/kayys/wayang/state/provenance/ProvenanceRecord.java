package tech.kayys.wayang.state.provenance;

import tech.kayys.wayang.state.artifact.ArtifactReference;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable record of how a subject (artifact or state) was produced.
 */
public record ProvenanceRecord(
        ProvenanceId id,
        ProvenanceSubject subject,
        ProvenanceOperation operation,
        List<ProvenanceInput> inputs,
        List<ArtifactReference> outputs,
        ProvenanceActor actor,
        ProvenanceEnvironment environment,
        Instant timestamp,
        Map<String, Object> metadata
) {
    public ProvenanceRecord {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(subject, "subject cannot be null");
        Objects.requireNonNull(operation, "operation cannot be null");
        inputs = inputs == null ? List.of() : List.copyOf(inputs);
        outputs = outputs == null ? List.of() : List.copyOf(outputs);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
