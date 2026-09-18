package tech.kayys.wayang.tool.validator;

import tech.kayys.wayang.tool.ToolExecutionContext;
import tech.kayys.wayang.tool.ToolResult;

/**
 * Validator contract for validating and potentially truncating tool outputs.
 */
public interface ToolOutputValidator {

    ToolResult validate(ToolResult result, ToolExecutionContext context);

    static ToolOutputValidator standard() {
        return new DefaultToolOutputValidator();
    }
}
