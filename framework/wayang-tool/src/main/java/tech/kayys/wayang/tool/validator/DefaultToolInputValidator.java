package tech.kayys.wayang.tool.validator;

import tech.kayys.wayang.tool.ToolArguments;
import tech.kayys.wayang.tool.schema.ToolInputSchema;

public class DefaultToolInputValidator implements ToolInputValidator {

    @Override
    public boolean validate(ToolArguments arguments, ToolInputSchema schema) {
        if (schema == null) {
            return true;
        }
        return schema.validate(arguments);
    }
}
