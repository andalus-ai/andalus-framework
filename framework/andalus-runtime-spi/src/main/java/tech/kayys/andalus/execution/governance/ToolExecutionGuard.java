package tech.kayys.andalus.execution.governance;

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

    CompletableFuture<ToolResult> executeApproved(
            String approvalId,
            ToolInvocation invocation,
            ToolContext toolContext,
            ToolExecutor delegate);
}
