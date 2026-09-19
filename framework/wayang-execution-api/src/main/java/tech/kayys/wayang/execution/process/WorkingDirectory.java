package tech.kayys.wayang.execution.process;

import java.util.Objects;

/**
 * Working directory relative to sandbox workspace or absolute path.
 */
public record WorkingDirectory(
        String path,
        boolean relativeToWorkspace
) {

    public WorkingDirectory {
        Objects.requireNonNull(path, "path cannot be null");
        path = path.trim();
    }

    public static WorkingDirectory workspaceRoot() {
        return new WorkingDirectory(".", true);
    }

    public static WorkingDirectory relative(String path) {
        return new WorkingDirectory(path, true);
    }

    public static WorkingDirectory absolute(String path) {
        return new WorkingDirectory(path, false);
    }
}
