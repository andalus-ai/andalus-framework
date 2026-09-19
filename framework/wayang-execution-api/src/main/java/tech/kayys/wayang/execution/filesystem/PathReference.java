package tech.kayys.wayang.execution.filesystem;

import java.util.Objects;

/**
 * Reference to a filesystem path being accessed.
 */
public record PathReference(
        String rawPath,
        boolean relativeToWorkspace
) {

    public PathReference {
        Objects.requireNonNull(rawPath, "rawPath cannot be null");
        rawPath = rawPath.trim();
    }

    public static PathReference workspace(String relativePath) {
        return new PathReference(relativePath, true);
    }

    public static PathReference absolute(String fullPath) {
        return new PathReference(fullPath, false);
    }
}
