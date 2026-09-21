package tech.kayys.andalus.tool.validator;

import tech.kayys.andalus.tool.ToolArguments;
import tech.kayys.andalus.tool.schema.ToolInputSchema;

public class DefaultToolInputValidator implements ToolInputValidator {

    @Override
    public boolean validate(ToolArguments arguments, ToolInputSchema schema) {
        if (schema == null) {
            return true;
        }
        return schema.validate(arguments);
    }
}
