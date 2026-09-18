package tech.kayys.wayang.tool;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public record DefaultToolInvocation(
        ToolInvocationId invocationIdentifier,
        ToolId toolId,
        ToolArguments toolArguments
) implements ToolInvocation {

    public DefaultToolInvocation {
        Objects.requireNonNull(invocationIdentifier, "ToolInvocationId cannot be null");
        Objects.requireNonNull(toolId, "ToolId cannot be null");
        if (toolArguments == null) {
            toolArguments = ToolArguments.empty();
        }
    }

    public static DefaultToolInvocation of(ToolId toolId, ToolArguments arguments) {
        return new DefaultToolInvocation(ToolInvocationId.generate(), toolId, arguments);
    }

    @Override
    public String name() {
        return toolId.value();
    }

    @Override
    public String invocationId() {
        return invocationIdentifier.value();
    }

    @Override
    public Map<String, Object> arguments() {
        return toolArguments.asMap();
    }
}
