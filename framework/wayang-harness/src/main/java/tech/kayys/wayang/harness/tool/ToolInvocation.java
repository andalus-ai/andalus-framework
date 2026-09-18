package tech.kayys.wayang.harness.tool;

import java.util.Objects;

/**
 * Represents a tool invocation.
 *
 * <p>Its components capture `id`, `tool id`, `arguments`.</p>
 *
 * @param id the id
 * @param toolId the tool id
 * @param arguments the arguments
 */


public record ToolInvocation(
        ToolInvocationId id,
        ToolId toolId,
        ToolArguments arguments
) {
    public ToolInvocation {
        Objects.requireNonNull(id, "ToolInvocationId cannot be null");
        Objects.requireNonNull(toolId, "ToolId cannot be null");
        if (arguments == null) {
            arguments = ToolArguments.empty();
        }
    }

    public static ToolInvocation of(ToolId toolId, ToolArguments arguments) {
        return new ToolInvocation(ToolInvocationId.generate(), toolId, arguments);
    }
}
