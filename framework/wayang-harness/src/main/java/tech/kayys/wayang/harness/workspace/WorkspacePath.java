package tech.kayys.wayang.harness.workspace;

import java.util.Objects;

/**
 * Sandboxed path abstraction preventing path traversal outside the workspace root.
 */
public record WorkspacePath(String value) {

    public WorkspacePath {
        Objects.requireNonNull(value, "value");
        String normalized = value.replace('\\', '/').replaceAll("^/+", "");
        if (normalized.equals("..") || normalized.startsWith("../") || normalized.contains("/../")) {
            throw new IllegalArgumentException("Path traversal outside workspace root is forbidden: " + value);
        }
        value = normalized;
    }

    public static WorkspacePath of(String value) {
        return new WorkspacePath(value);
    }

    public static WorkspacePath root() {
        return new WorkspacePath("");
    }

    public WorkspacePath resolve(String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath");
        String combined = value.isEmpty() ? relativePath : value + "/" + relativePath;
        return new WorkspacePath(combined);
    }

    public boolean isRoot() {
        return value.isEmpty();
    }
}
