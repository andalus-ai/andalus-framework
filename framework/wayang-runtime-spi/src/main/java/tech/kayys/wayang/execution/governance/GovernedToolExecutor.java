package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolContext;
import tech.kayys.wayang.tool.ToolExecutor;
import tech.kayys.wayang.tool.ToolInvocation;
import tech.kayys.wayang.tool.ToolResult;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Decorator wrapping a {@link ToolExecutor} with governance enforcement via {@link ToolExecutionGuard}.
 */
public final class GovernedToolExecutor implements ToolExecutor {

    private final ToolExecutor delegate;
    private final ToolExecutionGuard guard;

    public GovernedToolExecutor(ToolExecutor delegate, ToolExecutionGuard guard) {
        this.delegate = Objects.requireNonNull(delegate, "delegate cannot be null");
        this.guard = Objects.requireNonNull(guard, "guard cannot be null");
    }

    @Override
    public CompletableFuture<ToolResult> execute(ToolInvocation invocation, ToolContext context) {
        return guard.execute(invocation, context, delegate);
    }
}
