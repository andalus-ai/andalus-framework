package tech.kayys.wayang.harness.tool;

/**
 * Provides the default implementation of the tool input validator contract.
 */


public class DefaultToolInputValidator implements ToolInputValidator {

    @Override
    public boolean validate(ToolArguments arguments, ToolInputSchema schema) {
        if (schema == null) {
            return true;
        }
        return schema.validate(arguments);
    }
}
