package tech.kayys.wayang.harness.tool;

/**
 * Defines the contract for tool input validator operations in the Wayang framework.
 */


public interface ToolInputValidator {

    boolean validate(ToolArguments arguments, ToolInputSchema schema);

    static ToolInputValidator standard() {
        return new DefaultToolInputValidator();
    }
}
