package tech.kayys.wayang.execution.core.sandbox;

import tech.kayys.wayang.execution.core.workspace.LocalWorkspace;
import tech.kayys.wayang.execution.sandbox.*;
import tech.kayys.wayang.execution.workspace.WorkspaceHandle;
import tech.kayys.wayang.execution.workspace.WorkspaceId;
import tech.kayys.wayang.execution.workspace.WorkspaceMode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Standard implementation of {@link SandboxHandle}.
 */
public class DefaultSandboxHandle implements SandboxHandle {

    private final SandboxId id;
    private final SandboxSpec specification;
    private final WorkspaceHandle workspace;
    private volatile SandboxState state = SandboxState.CREATING;

    public DefaultSandboxHandle(SandboxId id, SandboxSpec specification) {
        this.id = Objects.requireNonNull(id, "SandboxId cannot be null");
        this.specification = Objects.requireNonNull(specification, "SandboxSpec cannot be null");

        try {
            Path tempDir = Files.createTempDirectory("wayang-sbx-" + id.value());
            this.workspace = new LocalWorkspace(
                    WorkspaceId.of("ws-" + id.value()),
                    specification.workspace() != null ? specification.workspace().mode() : WorkspaceMode.EPHEMERAL,
                    tempDir
            );
            this.state = SandboxState.READY;
        } catch (Exception e) {
            this.state = SandboxState.FAILED;
            throw new IllegalStateException("Failed to initialize sandbox workspace", e);
        }
    }

    @Override
    public SandboxId id() {
        return id;
    }

    @Override
    public SandboxState state() {
        return state;
    }

    @Override
    public SandboxSpec specification() {
        return specification;
    }

    @Override
    public ExecutionSandbox sandbox() {
        return new DefaultExecutionSandbox(id, specification, state);
    }

    @Override
    public WorkspaceHandle workspace() {
        return workspace;
    }

    @Override
    public synchronized void start() throws Exception {
        if (state == SandboxState.DESTROYED || state == SandboxState.FAILED) {
            throw new IllegalStateException("Cannot start sandbox in state: " + state);
        }
        if (specification.lifecycle() != null) {
            for (SandboxLifecycleHook hook : specification.lifecycle().hooks()) {
                hook.beforeStart(sandbox());
            }
        }
        this.state = SandboxState.RUNNING;
        if (specification.lifecycle() != null) {
            for (SandboxLifecycleHook hook : specification.lifecycle().hooks()) {
                hook.afterStart(sandbox());
            }
        }
    }

    @Override
    public synchronized void pause() throws Exception {
        if (state == SandboxState.RUNNING) {
            this.state = SandboxState.PAUSED;
        }
    }

    @Override
    public synchronized void destroy() throws Exception {
        if (state == SandboxState.DESTROYED) {
            return;
        }
        if (specification.lifecycle() != null) {
            for (SandboxLifecycleHook hook : specification.lifecycle().hooks()) {
                hook.beforeDestroy(sandbox());
            }
        }
        this.state = SandboxState.STOPPING;
        try {
            if (workspace != null) {
                workspace.close();
            }
        } finally {
            this.state = SandboxState.DESTROYED;
            if (specification.lifecycle() != null) {
                for (SandboxLifecycleHook hook : specification.lifecycle().hooks()) {
                    hook.afterDestroy(id);
                }
            }
        }
    }
}
