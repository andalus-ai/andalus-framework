package tech.kayys.wayang.execution.artifact;

import tech.kayys.wayang.execution.sandbox.SandboxId;

/**
 * Imports external artifacts into a designated target path inside a sandbox.
 */
public interface ArtifactImporter {

    void importArtifact(SandboxId sandboxId, ArtifactReference artifact, String targetPath);
}
