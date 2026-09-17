package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolContext;
import tech.kayys.wayang.tool.ToolExecutor;
import tech.kayys.wayang.tool.ToolInvocation;
import tech.kayys.wayang.tool.ToolResult;

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
