package tech.kayys.wayang.tool;

import java.time.Duration;

/**
 * Execution policy controlling timeouts, retries, and isolation for tool invocation.
 */
public record ToolExecutionPolicy(
        Duration timeout,
        int retryCount,
        boolean requiresApproval,
        String isolationLevel
) {
    public static ToolExecutionPolicy defaults() {
        return new ToolExecutionPolicy(Duration.ofSeconds(30), 0, false, "STANDARD");
    }

    public static ToolExecutionPolicy withTimeout(Duration timeout) {
        return new ToolExecutionPolicy(timeout, 0, false, "STANDARD");
    }

    public static ToolExecutionPolicy strict(Duration timeout) {
        return new ToolExecutionPolicy(timeout, 0, true, "STRICT");
    }
}
