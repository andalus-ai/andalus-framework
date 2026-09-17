package tech.kayys.wayang.harness.workspace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local workspace manager provisioning filesystem workspaces under a designated base directory.
 */
public class LocalWorkspaceManager implements WorkspaceManager {

    private final Path baseStorageDir;
    private final Map<WorkspaceId, Workspace> activeWorkspaces = new ConcurrentHashMap<>();

    public LocalWorkspaceManager() {
        try {
            this.baseStorageDir = Files.createTempDirectory("wayang-workspaces");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize default workspace storage directory", e);
        }
    }

    public LocalWorkspaceManager(Path baseStorageDir) {
        this.baseStorageDir = Objects.requireNonNull(baseStorageDir, "baseStorageDir").toAbsolutePath().normalize();
    }

    @Override
    public Workspace create(WorkspaceRequest request) {
        Objects.requireNonNull(request, "request");
        WorkspaceId id = WorkspaceId.generate();
        Path wsDir = baseStorageDir.resolve(id.value());

        Workspace ws = new LocalWorkspace(id, request.type(), request.policy(), wsDir, request.ephemeral());
        activeWorkspaces.put(id, ws);
        return ws;
    }

    @Override
    public Workspace attach(WorkspaceId id) {
        Objects.requireNonNull(id, "id");
        Workspace ws = activeWorkspaces.get(id);
        if (ws == null) {
            throw new NoSuchElementException("Workspace not found or not active: " + id.value());
        }
        return ws;
    }

    @Override
    public void release(WorkspaceId id) {
        Objects.requireNonNull(id, "id");
        Workspace ws = activeWorkspaces.remove(id);
        if (ws != null) {
            ws.release();
        }
    }

    public Path baseStorageDir() {
        return baseStorageDir;
    }
}
