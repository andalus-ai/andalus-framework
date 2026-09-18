package tech.kayys.wayang.harness.tool;

public interface ToolOutputValidator {

    ToolResult validate(
            ToolResult result,
            ToolExecutionContext context
    );

    static ToolOutputValidator standard() {
        return new DefaultToolOutputValidator();
    }
}
