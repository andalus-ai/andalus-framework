package tech.kayys.wayang.harness.workspace.v3;

import tech.kayys.wayang.harness.artifact.DiffArtifact;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.workspace.WorkspaceId;

/**
 * SPI for calculating diffs and creating patch artifacts from workspaces.
 */
public interface WorkspaceDiffService {

    DiffArtifact diff(ExecutionId executionId, WorkspaceId workspaceId, DiffBase base);
}
