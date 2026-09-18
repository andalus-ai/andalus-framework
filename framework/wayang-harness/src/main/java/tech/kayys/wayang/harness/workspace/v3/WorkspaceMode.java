package tech.kayys.wayang.harness.workspace.v3;

/**
 * Storage and isolation modes for managed workspaces.
 */
public enum WorkspaceMode {
    EPHEMERAL,
    PERSISTENT,
    CLONED,
    SNAPSHOT,
    READ_ONLY,
    SHARED_READ_ONLY
}
