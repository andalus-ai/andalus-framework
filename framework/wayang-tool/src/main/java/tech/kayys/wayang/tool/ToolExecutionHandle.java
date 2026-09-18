package tech.kayys.wayang.tool;

import java.util.concurrent.CompletableFuture;

/**
 * Handle representing an in-flight or completed tool execution.
 */
public interface ToolExecutionHandle {

    ToolInvocationId id();

    ToolExecutionStatus status();

    CompletableFuture<ToolResult> result();

    void cancel();
}
