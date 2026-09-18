package tech.kayys.wayang.harness.tool;

import java.util.concurrent.CompletableFuture;

public interface ToolExecutionHandle {

    ToolInvocationId id();

    ToolExecutionStatus status();

    CompletableFuture<ToolResult> result();

    void cancel();
}
