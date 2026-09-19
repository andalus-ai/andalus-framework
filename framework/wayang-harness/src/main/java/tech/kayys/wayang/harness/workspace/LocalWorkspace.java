package tech.kayys.wayang.harness.workspace;

import tech.kayys.wayang.execution.workspace.WorkspaceMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Local filesystem-backed workspace implementation delegating to the unified execution workspace.
 */
public class LocalWorkspace implements Workspace, WorkspaceHandle {

    private final WorkspaceId id;
    private final WorkspaceType type;
    private final WorkspacePolicy policy;
    private final Path rootDir;
    private final boolean ephemeral;
    private final tech.kayys.wayang.execution.core.workspace.LocalWorkspace delegate;
    private volatile boolean released = false;

    public LocalWorkspace(WorkspaceId id, WorkspaceType type, WorkspacePolicy policy, Path rootDir, boolean ephemeral) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.rootDir = Objects.requireNonNull(rootDir, "rootDir").toAbsolutePath().normalize();
        this.ephemeral = ephemeral;

        WorkspaceMode mode = policy.readOnly()
                ? WorkspaceMode.READ_ONLY
                : (ephemeral ? WorkspaceMode.EPHEMERAL : WorkspaceMode.PERSISTENT);
        this.delegate = new tech.kayys.wayang.execution.core.workspace.LocalWorkspace(
                new tech.kayys.wayang.execution.workspace.WorkspaceId(id.value()),
                mode,
                this.rootDir
        );
    }

    @Override
    public WorkspaceId id() {
        return id;
    }

    @Override
    public WorkspaceType type() {
        return type;
    }

    @Override
    public WorkspaceSnapshot snapshot() {
        checkNotReleased();
        try (Stream<Path> stream = Files.walk(rootDir)) {
            List<Path> files = stream.filter(Files::isRegularFile).toList();
            long totalBytes = 0;
            for (Path p : files) {
                totalBytes += Files.size(p);
            }
            return new WorkspaceSnapshot(
                    "snap-" + System.currentTimeMillis(),
                    "fp-" + files.size() + "-" + totalBytes,
                    Instant.now(),
                    totalBytes,
                    files.size(),
                    Map.of("root", rootDir.toString())
            );
        } catch (IOException e) {
            return WorkspaceSnapshot.empty();
        }
    }

    @Override
    public WorkspaceHandle acquire() {
        checkNotReleased();
        return this;
    }

    @Override
    public void release() {
        if (!released) {
            released = true;
            try {
                delegate.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public WorkspacePath root() {
        return WorkspacePath.root();
    }

    @Override
    public WorkspacePath resolve(String relativePath) {
        return root().resolve(relativePath);
    }

    @Override
    public InputStream read(WorkspacePath path) throws IOException {
        checkNotReleased();
        try {
            return delegate.read(path.value());
        } catch (IllegalArgumentException e) {
            throw new SecurityException("Path traversal attempt detected: " + path.value(), e);
        }
    }

    @Override
    public OutputStream write(WorkspacePath path) throws IOException {
        checkNotReleased();
        if (policy.readOnly()) {
            throw new SecurityException("Workspace policy is read-only");
        }
        try {
            return delegate.write(path.value());
        } catch (IllegalArgumentException e) {
            throw new SecurityException("Path traversal attempt detected: " + path.value(), e);
        }
    }

    @Override
    public boolean exists(WorkspacePath path) {
        checkNotReleased();
        try {
            return delegate.exists(path.value());
        } catch (IllegalArgumentException | SecurityException e) {
            return false;
        }
    }

    @Override
    public List<WorkspacePath> list(WorkspacePath path) throws IOException {
        checkNotReleased();
        try {
            List<String> relativePaths = delegate.list(path.value());
            return relativePaths.stream().map(WorkspacePath::of).toList();
        } catch (IllegalArgumentException e) {
            throw new SecurityException("Path traversal attempt detected: " + path.value(), e);
        }
    }

    @Override
    public boolean delete(WorkspacePath path) throws IOException {
        checkNotReleased();
        if (policy.readOnly()) {
            throw new SecurityException("Workspace policy is read-only");
        }
        try {
            Path target = delegate.resolveContainedPath(path.value());
            return Files.deleteIfExists(target);
        } catch (IllegalArgumentException e) {
            throw new SecurityException("Path traversal attempt detected: " + path.value(), e);
        }
    }

    public Path rootDir() {
        return rootDir;
    }

    private void checkNotReleased() {
        if (released) {
            throw new IllegalStateException("Workspace " + id.value() + " has already been released");
        }
    }
}
