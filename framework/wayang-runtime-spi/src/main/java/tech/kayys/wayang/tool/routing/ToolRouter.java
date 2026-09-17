package tech.kayys.wayang.tool.routing;

import tech.kayys.wayang.tool.ToolContext;
import tech.kayys.wayang.tool.ToolInvocation;
import tech.kayys.wayang.tool.ToolResult;

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
