package tech.kayys.wayang.harness.tool;

public interface ToolInputValidator {

    boolean validate(ToolArguments arguments, ToolInputSchema schema);

    static ToolInputValidator standard() {
        return new DefaultToolInputValidator();
    }
}
