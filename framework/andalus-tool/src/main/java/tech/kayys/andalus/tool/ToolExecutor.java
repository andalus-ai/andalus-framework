package tech.kayys.andalus.tool;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@FunctionalInterface
public interface ToolExecutor {

    CompletableFuture<ToolResult> execute(ToolInvocation invocation, ToolContext context);

    /**
     * Executes through this executor with cooperative cancellation.
     * Implementations that own asynchronous work should override this method
     * and propagate the token into the dispatched operation.
     */
    default CompletableFuture<ToolResult> execute(
            ToolInvocation invocation,
            ToolContext context,
            CancellationToken cancellation) {
        if (cancellation != null && cancellation.isCancellationRequested()) {
            return CompletableFuture.failedFuture(
                    new java.util.concurrent.CancellationException("Tool execution cancelled before dispatch."));
        }
        CompletableFuture<ToolResult> result = execute(invocation, context);
        if (cancellation == null) {
            return result;
        }
        return result.thenApply(value -> {
            if (cancellation.isCancellationRequested()) {
                throw new java.util.concurrent.CancellationException("Tool execution cancelled.");
            }
            return value;
        });
    }

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

    /**
     * Blocking adapter that preserves the execution-scoped cancellation signal.
     * Implementations with cancellable asynchronous work receive the same token
     * through the three-argument execute method.
     */
    default ToolResult executeBlocking(
            ToolInvocation invocation,
            ToolExecutionContext context,
            CancellationToken cancellation) {
        if (cancellation != null && cancellation.isCancellationRequested()) {
            return ToolResult.canceled(invocation.invocationIdentifier());
        }
        try {
            return execute(invocation, context, cancellation).get();
        } catch (InterruptedException interrupted) {
            if (cancellation != null) cancellation.cancel();
            Thread.currentThread().interrupt();
            return ToolResult.canceled(invocation.invocationIdentifier());
        } catch (java.util.concurrent.CancellationException cancelled) {
            return ToolResult.canceled(invocation.invocationIdentifier());
        } catch (Exception failure) {
            Throwable cause = failure.getCause() != null ? failure.getCause() : failure;
            if (cause instanceof java.util.concurrent.CancellationException) {
                return ToolResult.canceled(invocation.invocationIdentifier());
            }
            return ToolResult.failure(invocation.invocationIdentifier(),
                    cause.getMessage(), Duration.ZERO, invocation.name());
        }
    }

    static ToolExecutor synchronous(BiFunction<ToolInvocation, ToolContext, ToolResult> syncFn) {
        return (inv, ctx) -> CompletableFuture.completedFuture(syncFn.apply(inv, ctx));
    }
}
