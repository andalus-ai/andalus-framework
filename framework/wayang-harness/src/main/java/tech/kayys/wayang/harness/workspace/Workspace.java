package tech.kayys.wayang.harness.workspace;

/**
 * Represents an allocated workspace boundary within which an agent operates.
 */
public interface Workspace {

    WorkspaceId id();

    WorkspaceType type();

    WorkspaceSnapshot snapshot();

    WorkspaceHandle acquire();

    void release();
}
