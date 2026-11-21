package tech.kayys.andalus.execution.governance;

import tech.kayys.andalus.tool.CancellationToken;
import tech.kayys.andalus.tool.ToolContext;
import tech.kayys.andalus.tool.ToolExecutor;
import tech.kayys.andalus.tool.ToolInvocation;
import tech.kayys.andalus.tool.ToolResult;

import java.util.concurrent.CompletableFuture;

/**
 * Gatekeeper ensuring tool execution complies with policies, authorization rules, and approvals.
 */
public interface ToolExecutionGuard {

    CompletableFuture<ToolResult> execute(
            ToolInvocation invocation,
            ToolContext toolContext,
            ToolExecutor delegate);

    /**
     * Cancellation-aware execution entry point. Implementations should propagate the
     * token through policy-approved dispatch; this default preserves compatibility.
     */
    default CompletableFuture<ToolResult> execute(
            ToolInvocation invocation,
            ToolContext toolContext,
            ToolExecutor delegate,
            CancellationToken cancellation) {
        if (cancellation != null && cancellation.isCancellationRequested()) {
            return CompletableFuture.failedFuture(
                    new java.util.concurrent.CancellationException("Tool execution cancelled before governance."));
        }
        CompletableFuture<ToolResult> result = execute(invocation, toolContext, delegate);
        if (cancellation == null) return result;
        return result.thenApply(value -> {
            if (cancellation.isCancellationRequested()) {
                throw new java.util.concurrent.CancellationException("Tool execution cancelled.");
            }
            return value;
        });
    }

    CompletableFuture<ToolResult> executeApproved(
            String approvalId,
            ToolInvocation invocation,
            ToolContext toolContext,
            ToolExecutor delegate);
}
