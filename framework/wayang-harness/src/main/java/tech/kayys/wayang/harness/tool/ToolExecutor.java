package tech.kayys.wayang.harness.tool;

/**
 * Defines the contract for tool executor operations in the Wayang framework.
 */


@FunctionalInterface
public interface ToolExecutor {

    ToolResult execute(
            ToolInvocation invocation,
            ToolExecutionContext context
    );
}
