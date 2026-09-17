package tech.kayys.wayang.harness.workspace;

/**
 * SPI for managing creation, attachment, and release of workspaces.
 */
public interface WorkspaceManager {

    Workspace create(WorkspaceRequest request);

    Workspace attach(WorkspaceId id);

    void release(WorkspaceId id);
}
