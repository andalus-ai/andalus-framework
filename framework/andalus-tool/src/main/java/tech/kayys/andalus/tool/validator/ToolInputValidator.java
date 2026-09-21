package tech.kayys.andalus.tool.validator;

import tech.kayys.andalus.tool.ToolArguments;
import tech.kayys.andalus.tool.schema.ToolInputSchema;

/**
 * Validator contract for validating tool input arguments against a schema.
 */
public interface ToolInputValidator {

    boolean validate(ToolArguments arguments, ToolInputSchema schema);

    static ToolInputValidator standard() {
        return new DefaultToolInputValidator();
    }
}
