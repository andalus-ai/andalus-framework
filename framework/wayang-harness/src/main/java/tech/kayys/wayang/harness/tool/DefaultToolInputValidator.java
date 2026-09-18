package tech.kayys.wayang.harness.tool;

public class DefaultToolInputValidator implements ToolInputValidator {

    @Override
    public boolean validate(ToolArguments arguments, ToolInputSchema schema) {
        if (schema == null) {
            return true;
        }
        return schema.validate(arguments);
    }
}
