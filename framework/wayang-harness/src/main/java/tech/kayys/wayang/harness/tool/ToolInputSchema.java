package tech.kayys.wayang.harness.tool;

import java.util.Map;
import java.util.Set;

public interface ToolInputSchema {

    SchemaType rootType();

    Map<String, SchemaProperty> properties();

    Set<String> required();

    default boolean validate(ToolArguments arguments) {
        if (arguments == null) {
            return required().isEmpty();
        }
        for (String req : required()) {
            if (arguments.value(req) == null) {
                return false;
            }
        }
        return true;
    }
}
