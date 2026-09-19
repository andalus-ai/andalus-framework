package tech.kayys.wayang.execution.workspace;

import java.nio.file.Path;

/**
 * Handle to an allocated or attached sandbox workspace.
 */
public interface WorkspaceHandle extends AutoCloseable {

    WorkspaceId id();

    WorkspaceMode mode();

    Path rootDirectory();

    boolean isAvailable();

    @Override
    default void close() throws Exception {
        // Default no-op
    }
}
