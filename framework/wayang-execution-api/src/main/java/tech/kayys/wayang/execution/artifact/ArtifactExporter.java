package tech.kayys.wayang.execution.artifact;

import tech.kayys.wayang.execution.sandbox.SandboxId;

import java.util.List;

/**
 * Exports generated files from a sandbox to first-class external artifacts.
 */
public interface ArtifactExporter {

    List<ArtifactReference> exportArtifacts(SandboxId sandboxId, ArtifactExportPolicy policy);
}
