package tech.kayys.andalus.tool;

import tech.kayys.andalus.extension.Extension;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.tool.capability.Capability;

public interface Tool extends Extension {
    ToolDescriptor descriptor();
    CompletableFuture<ToolResult> execute(ToolInvocation invocation, ToolContext context);

    /**
     * Executes this tool with the owning agent's cooperative cancellation signal.
     *
     * <p>The compatibility implementation checks cancellation before dispatch and
     * after completion. Tools that perform long-running or interruptible work should
     * override this method and observe the token while doing that work.</p>
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

    /**
     * Returns the set of capabilities (e.g., DomainScope, RequiresNetwork) exposed by this tool.
     */
    default Collection<Capability> toolCapabilities() {
        return Collections.emptyList();
    }
}
