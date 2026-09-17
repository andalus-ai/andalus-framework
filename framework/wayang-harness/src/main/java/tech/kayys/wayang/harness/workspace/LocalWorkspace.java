package tech.kayys.wayang.harness.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Local filesystem-backed workspace implementation enforcing directory containment.
 */
public class LocalWorkspace implements Workspace, WorkspaceHandle {

    private final WorkspaceId id;
    private final WorkspaceType type;
    private final WorkspacePolicy policy;
    private final Path rootDir;
    private final boolean ephemeral;
    private volatile boolean released = false;

    public LocalWorkspace(WorkspaceId id, WorkspaceType type, WorkspacePolicy policy, Path rootDir, boolean ephemeral) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.rootDir = Objects.requireNonNull(rootDir, "rootDir").toAbsolutePath().normalize();
        this.ephemeral = ephemeral;

        try {
            Files.createDirectories(this.rootDir);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create workspace root directory: " + rootDir, e);
        }
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
            if (ephemeral && Files.exists(rootDir)) {
                try (Stream<Path> stream = Files.walk(rootDir)) {
                    stream.sorted(Comparator.reverseOrder()).forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                        }
                    });
                } catch (IOException ignored) {
                }
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
        Path target = toSecurePath(path);
        if (!Files.exists(target)) {
            throw new IOException("File not found in workspace: " + path.value());
        }
        return Files.newInputStream(target);
    }

    @Override
    public OutputStream write(WorkspacePath path) throws IOException {
        checkNotReleased();
        if (policy.readOnly()) {
            throw new SecurityException("Workspace policy is read-only");
        }
        Path target = toSecurePath(path);
        if (target.getParent() != null) {
            Files.createDirectories(target.getParent());
        }
        return Files.newOutputStream(target);
    }

    @Override
    public boolean exists(WorkspacePath path) {
        checkNotReleased();
        try {
            Path target = toSecurePath(path);
            return Files.exists(target);
        } catch (SecurityException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public List<WorkspacePath> list(WorkspacePath path) throws IOException {
        checkNotReleased();
        Path target = toSecurePath(path);
        if (!Files.exists(target) || !Files.isDirectory(target)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.list(target)) {
            return stream.map(p -> {
                Path rel = rootDir.relativize(p);
                return WorkspacePath.of(rel.toString());
            }).toList();
        }
    }

    @Override
    public boolean delete(WorkspacePath path) throws IOException {
        checkNotReleased();
        if (policy.readOnly()) {
            throw new SecurityException("Workspace policy is read-only");
        }
        Path target = toSecurePath(path);
        return Files.deleteIfExists(target);
    }

    public Path rootDir() {
        return rootDir;
    }

    private Path toSecurePath(WorkspacePath path) {
        Path target = rootDir.resolve(path.value()).normalize();
        if (!target.startsWith(rootDir)) {
            throw new SecurityException("Path traversal attempt detected: " + path.value());
        }
        return target;
    }

    private void checkNotReleased() {
        if (released) {
            throw new IllegalStateException("Workspace " + id.value() + " has already been released");
        }
    }
}
