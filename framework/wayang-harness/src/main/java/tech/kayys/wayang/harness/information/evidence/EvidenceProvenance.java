package tech.kayys.wayang.harness.information.evidence;

import tech.kayys.wayang.harness.artifact.ArtifactId;
import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.util.Objects;
import java.util.Optional;

/**
 * Audit and provenance tracking the origin of evidence or factual data.
 */
public record EvidenceProvenance(
        String sourceUri,
        Optional<ArtifactId> sourceArtifactId,
        Optional<ExecutionId> producingExecutionId,
        double confidenceScore
) {
    public EvidenceProvenance {
        Objects.requireNonNull(sourceUri, "sourceUri");
        sourceArtifactId = sourceArtifactId != null ? sourceArtifactId : Optional.empty();
        producingExecutionId = producingExecutionId != null ? producingExecutionId : Optional.empty();
    }

    public static EvidenceProvenance direct(String sourceUri) {
        return new EvidenceProvenance(sourceUri, Optional.empty(), Optional.empty(), 1.0);
    }
}
