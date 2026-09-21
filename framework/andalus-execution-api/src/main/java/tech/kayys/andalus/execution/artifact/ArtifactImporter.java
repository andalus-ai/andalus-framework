package tech.kayys.andalus.execution.artifact;

import tech.kayys.andalus.execution.sandbox.SandboxId;

/**
 * Imports external artifacts into a designated target path inside a sandbox.
 */
public interface ArtifactImporter {

    void importArtifact(SandboxId sandboxId, ArtifactReference artifact, String targetPath);
}
