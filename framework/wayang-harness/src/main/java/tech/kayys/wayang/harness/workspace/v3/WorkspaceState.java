package tech.kayys.wayang.harness.workspace.v3;

/**
 * Operating states of a managed workspace in Harness v3.1.
 */
public enum WorkspaceState {
    CREATING,
    READY,
    ACTIVE,
    FROZEN,
    EXPORTING,
    IMPORTING,
    FINALIZING,
    RETAINED,
    DELETED,
    FAILED
}
