package tech.kayys.wayang.state.lineage;

import tech.kayys.wayang.state.artifact.ArtifactId;
import tech.kayys.wayang.state.provenance.OperationId;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record ArtifactLineage(
        ArtifactId output,
        List<ArtifactId> inputs,
        OperationId operation,
        Instant timestamp
) {
    public ArtifactLineage {
        Objects.requireNonNull(output, "output cannot be null");
        inputs = inputs == null ? List.of() : List.copyOf(inputs);
    }
}
