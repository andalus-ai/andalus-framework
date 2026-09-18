package tech.kayys.wayang.harness.tool;

@FunctionalInterface
public interface ToolExecutor {

    ToolResult execute(
            ToolInvocation invocation,
            ToolExecutionContext context
    );
}
