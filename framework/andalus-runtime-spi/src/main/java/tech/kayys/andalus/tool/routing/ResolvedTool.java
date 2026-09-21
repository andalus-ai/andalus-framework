package tech.kayys.andalus.tool.routing;

import tech.kayys.andalus.tool.Tool;
import tech.kayys.andalus.tool.ToolExecutor;

import java.util.Objects;

/**
 * Result of resolving a tool invocation to a specific tool, executor, and provider.
 */
public record ResolvedTool(
        Tool tool,
        ToolExecutor executor,
        String providerId
) {

    public ResolvedTool {
        Objects.requireNonNull(tool, "tool cannot be null");
        Objects.requireNonNull(executor, "executor cannot be null");

        if (providerId == null || providerId.isBlank()) {
            throw new IllegalArgumentException("providerId cannot be null or blank");
        }
        providerId = providerId.trim();
    }
}
