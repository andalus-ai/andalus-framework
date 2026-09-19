package tech.kayys.wayang.state.provenance;

import tech.kayys.wayang.state.artifact.ArtifactReference;

import java.util.Map;
import java.util.Objects;

public record ProvenanceInput(
        String name,
        ArtifactReference reference,
        Map<String, Object> metadata
) {
    public ProvenanceInput {
        Objects.requireNonNull(name, "name cannot be null");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ProvenanceInput of(String name, ArtifactReference reference) {
        return new ProvenanceInput(name, reference, Map.of());
    }
}
