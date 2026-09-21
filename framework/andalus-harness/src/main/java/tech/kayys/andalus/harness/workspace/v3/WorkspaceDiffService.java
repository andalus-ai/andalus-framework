package tech.kayys.andalus.harness.workspace.v3;

import tech.kayys.andalus.harness.artifact.DiffArtifact;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.workspace.WorkspaceId;

/**
 * SPI for calculating diffs and creating patch artifacts from workspaces.
 */
public interface WorkspaceDiffService {

    DiffArtifact diff(ExecutionId executionId, WorkspaceId workspaceId, DiffBase base);
}
