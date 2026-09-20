package tech.kayys.wayang.execution.mobility.workspace;

import java.util.Optional;

/**
 * Store for capturing and restoring workspace snapshots.
 */
public interface WorkspaceStore {

    /**
     * Create a snapshot of the given workspace.
     */
    WorkspaceSnapshot snapshot(String workspaceId);

    /**
     * Restore a workspace snapshot into a target workspace.
     */
    void restore(WorkspaceSnapshot snapshot, String targetWorkspaceId);

    /**
     * Get the latest snapshot of a workspace if present.
     */
    Optional<WorkspaceSnapshot> getLatest(String workspaceId);
}
