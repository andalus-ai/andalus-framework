package tech.kayys.wayang.harness.tool;

import java.util.concurrent.CompletableFuture;

/**
 * Defines the contract for tool execution handle operations in the Wayang framework.
 */


public interface ToolExecutionHandle {

    ToolInvocationId id();

    ToolExecutionStatus status();

    CompletableFuture<ToolResult> result();

    void cancel();
}
