package tech.kayys.andalus.tool.routing;

import tech.kayys.andalus.tool.ToolContext;
import tech.kayys.andalus.tool.ToolInvocation;
import tech.kayys.andalus.tool.ToolResult;

import java.util.concurrent.CompletableFuture;

/**
 * Entry point for governed tool execution and provider resolution.
 */
public interface ToolRouter {

    CompletableFuture<ToolResult> execute(
            ToolInvocation invocation,
            ToolContext context);

    CompletableFuture<ToolResult> executeApproved(
            String approvalId,
            ToolInvocation invocation,
            ToolContext context);
}
