package tech.kayys.wayang.harness.tool;

import java.util.Objects;

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
