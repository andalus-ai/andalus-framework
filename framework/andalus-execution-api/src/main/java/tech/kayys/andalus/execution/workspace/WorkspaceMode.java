package tech.kayys.andalus.execution.workspace;

/**
 * Operating mode of a sandbox workspace.
 */
public enum WorkspaceMode {
    EPHEMERAL,
    PERSISTENT,
    SNAPSHOT,
    CLONED,
    ATTACHED,
    READ_ONLY
}
