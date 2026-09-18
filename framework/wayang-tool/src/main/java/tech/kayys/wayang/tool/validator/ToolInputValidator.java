package tech.kayys.wayang.tool.validator;

import tech.kayys.wayang.tool.ToolArguments;
import tech.kayys.wayang.tool.schema.ToolInputSchema;

/**
 * Validator contract for validating tool input arguments against a schema.
 */
public interface ToolInputValidator {

    boolean validate(ToolArguments arguments, ToolInputSchema schema);

    static ToolInputValidator standard() {
        return new DefaultToolInputValidator();
    }
}
