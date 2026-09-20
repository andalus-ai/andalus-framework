package tech.kayys.wayang.execution.mobility.workspace;

import java.util.Objects;

/**
 * An individual file or directory entry in a workspace snapshot.
 */
public record WorkspaceEntry(
        String path,
        long size,
        String digest,
        boolean isDirectory
) {
    public WorkspaceEntry {
        Objects.requireNonNull(path, "path cannot be null");
        Objects.requireNonNull(digest, "digest cannot be null");
        if (size < 0) {
            throw new IllegalArgumentException("size cannot be negative");
        }
    }

    public static WorkspaceEntry file(String path, long size, String digest) {
        return new WorkspaceEntry(path, size, digest, false);
    }

    public static WorkspaceEntry directory(String path) {
        return new WorkspaceEntry(path, 0L, "dir", true);
    }
}
