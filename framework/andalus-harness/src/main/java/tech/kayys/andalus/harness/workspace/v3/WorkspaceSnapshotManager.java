package tech.kayys.andalus.harness.workspace.v3;

import tech.kayys.andalus.harness.workspace.Workspace;
import tech.kayys.andalus.harness.workspace.WorkspaceId;

import java.util.Optional;

/**
 * SPI for managing point-in-time workspace snapshots and rollbacks.
 */
public interface WorkspaceSnapshotManager {

    WorkspaceSnapshot create(WorkspaceId workspaceId, SnapshotOptions options);

    Optional<WorkspaceSnapshot> find(SnapshotId snapshotId);

    void restore(SnapshotId snapshotId, Workspace destination);

    void delete(SnapshotId snapshotId);
}
