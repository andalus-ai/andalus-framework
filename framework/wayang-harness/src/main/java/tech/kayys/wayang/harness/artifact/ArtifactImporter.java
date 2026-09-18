package tech.kayys.wayang.harness.artifact;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

/**
 * SPI for importing artifacts from external sources.
 */
public interface ArtifactImporter {

    Artifact importArtifact(ExecutionId executionId, ImportRequest request);
}
