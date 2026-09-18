package tech.kayys.wayang.tool.schema;

import tech.kayys.wayang.tool.ToolArguments;

import java.util.Map;
import java.util.Set;

/**
 * Contract defining input schema expectations and validation for a tool.
 */
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
