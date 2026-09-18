package tech.kayys.wayang.harness.tool;

import java.util.Objects;

/**
 * Represents a tool intent.
 *
 * <p>Its components capture `id`, `capability`, `arguments`, `context`.</p>
 *
 * @param id the id
 * @param capability the capability
 * @param arguments the arguments
 * @param context the context
 */


public record ToolIntent(
        ToolIntentId id,
        String capability,
        ToolArguments arguments,
        ToolRequestContext context
) {
    public ToolIntent {
        Objects.requireNonNull(id, "ToolIntentId cannot be null");
        Objects.requireNonNull(capability, "capability cannot be null");
        if (arguments == null) {
            arguments = ToolArguments.empty();
        }
    }

    public static ToolIntent of(String capability, ToolArguments arguments, ToolRequestContext context) {
        return new ToolIntent(ToolIntentId.generate(), capability, arguments, context);
    }
}
