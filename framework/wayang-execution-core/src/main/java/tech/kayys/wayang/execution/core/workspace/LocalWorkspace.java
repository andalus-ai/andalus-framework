package tech.kayys.wayang.execution.core.workspace;

import tech.kayys.wayang.execution.workspace.WorkspaceHandle;
import tech.kayys.wayang.execution.workspace.WorkspaceId;
import tech.kayys.wayang.execution.workspace.WorkspaceMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Local filesystem-backed implementation of {@link WorkspaceHandle} enforcing path containment.
 */
public class LocalWorkspace implements WorkspaceHandle {

    private final WorkspaceId id;
    private final WorkspaceMode mode;
    private final Path rootDirectory;
    private volatile boolean closed = false;

    public LocalWorkspace(WorkspaceId id, WorkspaceMode mode, Path rootDirectory) {
        this.id = Objects.requireNonNull(id, "WorkspaceId cannot be null");
        this.mode = mode != null ? mode : WorkspaceMode.EPHEMERAL;
        this.rootDirectory = Objects.requireNonNull(rootDirectory, "rootDirectory cannot be null")
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.rootDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create workspace root directory: " + this.rootDirectory, e);
        }
    }

    @Override
    public WorkspaceId id() {
        return id;
    }

    @Override
    public WorkspaceMode mode() {
        return mode;
    }

    @Override
    public Path rootDirectory() {
        return rootDirectory;
    }

    @Override
    public boolean isAvailable() {
        return !closed && Files.exists(rootDirectory);
    }

    public Path resolveContainedPath(String relativePath) {
        checkAvailable();
        Objects.requireNonNull(relativePath, "relativePath cannot be null");
        Path resolved = rootDirectory.resolve(relativePath).normalize();
        if (!resolved.startsWith(rootDirectory)) {
            throw new IllegalArgumentException("Path traversal blocked: " + relativePath);
        }
        return resolved;
    }

    public OutputStream write(String relativePath) throws IOException {
        if (mode == WorkspaceMode.READ_ONLY) {
            throw new IllegalStateException("Cannot write to a READ_ONLY workspace");
        }
        Path target = resolveContainedPath(relativePath);
        if (target.getParent() != null) {
            Files.createDirectories(target.getParent());
        }
        return Files.newOutputStream(target);
    }

    public InputStream read(String relativePath) throws IOException {
        Path target = resolveContainedPath(relativePath);
        if (!Files.exists(target)) {
            throw new IOException("File does not exist: " + relativePath);
        }
        return Files.newInputStream(target);
    }

    public boolean exists(String relativePath) {
        Path target = resolveContainedPath(relativePath);
        return Files.exists(target);
    }

    public List<String> list(String subPath) throws IOException {
        Path target = resolveContainedPath(subPath);
        if (!Files.isDirectory(target)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.list(target)) {
            return stream.map(p -> rootDirectory.relativize(p).toString()).toList();
        }
    }

    @Override
    public void close() throws Exception {
        if (closed) {
            return;
        }
        closed = true;
        if (mode == WorkspaceMode.EPHEMERAL && Files.exists(rootDirectory)) {
            try (Stream<Path> walk = Files.walk(rootDirectory)) {
                walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException ignored) {}
                });
            }
        }
    }

    private void checkAvailable() {
        if (closed) {
            throw new IllegalStateException("Workspace has been closed: " + id.value());
        }
    }
}
