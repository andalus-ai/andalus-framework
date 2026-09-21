package tech.kayys.andalus.execution.artifact;

import tech.kayys.andalus.execution.sandbox.SandboxId;

import java.util.List;

/**
 * Exports generated files from a sandbox to first-class external artifacts.
 */
public interface ArtifactExporter {

    List<ArtifactReference> exportArtifacts(SandboxId sandboxId, ArtifactExportPolicy policy);
}
