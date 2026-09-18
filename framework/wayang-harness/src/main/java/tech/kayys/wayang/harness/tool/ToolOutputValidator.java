package tech.kayys.wayang.harness.tool;

/**
 * Defines the contract for tool output validator operations in the Wayang framework.
 */


public interface ToolOutputValidator {

    ToolResult validate(
            ToolResult result,
            ToolExecutionContext context
    );

    static ToolOutputValidator standard() {
        return new DefaultToolOutputValidator();
    }
}
