package tech.kayys.andalus.execution.sandbox;

import tech.kayys.andalus.execution.workspace.WorkspaceHandle;

/**
 * Handle to an allocated execution sandbox allowing lifecycle interactions.
 */
public interface SandboxHandle extends AutoCloseable {

    SandboxId id();

    SandboxState state();

    SandboxSpec specification();

    ExecutionSandbox sandbox();

    WorkspaceHandle workspace();

    void start() throws Exception;

    void pause() throws Exception;

    void destroy() throws Exception;

    @Override
    default void close() throws Exception {
        destroy();
    }
}
