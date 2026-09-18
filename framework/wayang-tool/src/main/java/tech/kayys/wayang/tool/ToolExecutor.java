package tech.kayys.wayang.tool;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@FunctionalInterface
public interface ToolExecutor {

    CompletableFuture<ToolResult> execute(ToolInvocation invocation, ToolContext context);

    default ToolResult executeBlocking(ToolInvocation invocation, ToolContext context) {
        try {
            return execute(invocation, context).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ToolResult.failure(invocation.invocationIdentifier(), e.getMessage(), Duration.ZERO, invocation.name());
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            return ToolResult.failure(invocation.invocationIdentifier(), cause.getMessage(), Duration.ZERO, invocation.name());
        }
    }

    static ToolExecutor synchronous(BiFunction<ToolInvocation, ToolContext, ToolResult> syncFn) {
        return (inv, ctx) -> CompletableFuture.completedFuture(syncFn.apply(inv, ctx));
    }
}
