package tech.kayys.andalus.execution.governance;

import tech.kayys.andalus.tool.SimpleToolResult;
import tech.kayys.andalus.tool.ToolContext;
import tech.kayys.andalus.tool.ToolExecutor;
import tech.kayys.andalus.tool.ToolInvocation;
import tech.kayys.andalus.tool.ToolResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public final class GovernanceTestToolExecutor implements ToolExecutor {

    private final AtomicInteger invocationCount = new AtomicInteger();

    @Override
    public CompletableFuture<ToolResult> execute(
            ToolInvocation invocation,
            ToolContext context) {

        invocationCount.incrementAndGet();

        return CompletableFuture.completedFuture(
                SimpleToolResult.success(Map.of("status", "executed"))
        );
    }

    public int invocationCount() {
        return invocationCount.get();
    }

    public void reset() {
        invocationCount.set(0);
    }
}
