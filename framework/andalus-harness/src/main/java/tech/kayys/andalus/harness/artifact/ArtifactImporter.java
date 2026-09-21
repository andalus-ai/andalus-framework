package tech.kayys.andalus.harness.artifact;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

/**
 * SPI for importing artifacts from external sources.
 */
public interface ArtifactImporter {

    Artifact importArtifact(ExecutionId executionId, ImportRequest request);
}
