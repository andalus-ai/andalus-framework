package tech.kayys.wayang.tool.routing;

import tech.kayys.wayang.execution.governance.ToolExecutionGuard;
import tech.kayys.wayang.tool.ToolContext;
import tech.kayys.wayang.tool.ToolInvocation;
import tech.kayys.wayang.tool.ToolResult;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Standard implementation of {@link ToolRouter} resolving tools and executing them via {@link ToolExecutionGuard}.
 */
public final class DefaultToolRouter implements ToolRouter {

    private final ToolResolver resolver;
    private final ToolExecutionGuard executionGuard;

    public DefaultToolRouter(ToolResolver resolver, ToolExecutionGuard executionGuard) {
        this.resolver = Objects.requireNonNull(resolver, "resolver cannot be null");
        this.executionGuard = Objects.requireNonNull(executionGuard, "executionGuard cannot be null");
    }

    @Override
    public CompletableFuture<ToolResult> execute(ToolInvocation invocation, ToolContext context) {
        Objects.requireNonNull(invocation, "invocation cannot be null");
        Objects.requireNonNull(context, "context cannot be null");

        ResolvedTool resolved = resolver.resolve(invocation);
        return executionGuard.execute(invocation, context, resolved.executor());
    }

    @Override
    public CompletableFuture<ToolResult> executeApproved(
            String approvalId,
            ToolInvocation invocation,
            ToolContext context) {

        Objects.requireNonNull(approvalId, "approvalId cannot be null");
        Objects.requireNonNull(invocation, "invocation cannot be null");
        Objects.requireNonNull(context, "context cannot be null");

        ResolvedTool resolved = resolver.resolve(invocation);
        return executionGuard.executeApproved(approvalId, invocation, context, resolved.executor());
    }
}
