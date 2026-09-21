package tech.kayys.andalus.tool.validator;

import tech.kayys.andalus.tool.ToolExecutionContext;
import tech.kayys.andalus.tool.ToolResult;

/**
 * Validator contract for validating and potentially truncating tool outputs.
 */
public interface ToolOutputValidator {

    ToolResult validate(ToolResult result, ToolExecutionContext context);

    static ToolOutputValidator standard() {
        return new DefaultToolOutputValidator();
    }
}
