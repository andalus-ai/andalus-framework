package tech.kayys.wayang.harness.tool;

import java.time.Duration;

public record ToolExecutionPolicy(
        Duration timeout,
        int maxRetries,
        boolean allowCancellation
) {
    public ToolExecutionPolicy {
        if (timeout == null) {
            timeout = Duration.ofSeconds(30);
        }
        if (maxRetries < 0) {
            maxRetries = 0;
        }
    }

    public static ToolExecutionPolicy defaults() {
        return new ToolExecutionPolicy(Duration.ofSeconds(30), 0, true);
    }

    public static ToolExecutionPolicy withTimeout(Duration timeout) {
        return new ToolExecutionPolicy(timeout, 0, true);
    }
}
