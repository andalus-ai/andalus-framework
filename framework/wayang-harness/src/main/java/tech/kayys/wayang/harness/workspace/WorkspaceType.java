package tech.kayys.wayang.harness.workspace;

/**
 * Classification of workspaces by backing medium and persistence.
 */
public enum WorkspaceType {
    LOCAL,
    EPHEMERAL,
    PERSISTENT,
    REMOTE,
    SANDBOX
}
