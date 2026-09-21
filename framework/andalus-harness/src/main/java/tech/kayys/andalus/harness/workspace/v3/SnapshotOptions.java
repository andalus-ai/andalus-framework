package tech.kayys.andalus.harness.workspace.v3;

import tech.kayys.andalus.harness.workspace.WorkspaceId;

import java.util.Optional;

/**
 * Options for taking a workspace snapshot.
 */
public record SnapshotOptions(
        boolean includeBuildArtifacts,
        boolean includeTemporaryFiles,
        String note
) {
    public static SnapshotOptions defaults() {
        return new SnapshotOptions(false, false, "Snapshot");
    }
}
