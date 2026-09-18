package tech.kayys.wayang.harness.tool;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a tool result.
 *
 * <p>Its components capture `invocation id`, `status`, `output`, `metadata`.</p>
 *
 * @param invocationId the invocation id
 * @param status the status
 * @param output the output
 * @param metadata the metadata
 */


public record ToolResult(
        ToolInvocationId invocationId,
        ToolResultStatus status,
        ToolOutput output,
        ToolMetadata metadata
) {
    public ToolResult {
        Objects.requireNonNull(invocationId, "invocationId cannot be null");
        Objects.requireNonNull(status, "status cannot be null");
        if (output == null) {
            output = ToolOutput.empty();
        }
        if (metadata == null) {
            metadata = ToolMetadata.empty();
        }
    }

    public boolean isSuccess() {
        return status == ToolResultStatus.SUCCESS;
    }

    public static ToolResult success(ToolInvocationId id, Object payload, Duration duration, String provider) {
        ToolOutput out = payload instanceof String str ? ToolOutput.text(str) : new ToolOutput(payload, 0, String.valueOf(payload), java.util.Optional.empty(), false);
        return new ToolResult(id, ToolResultStatus.SUCCESS, out, new ToolMetadata(duration, provider, Map.of()));
    }

    public static ToolResult failure(ToolInvocationId id, String errorMessage, Duration duration, String provider) {
        return new ToolResult(id, ToolResultStatus.FAILURE, ToolOutput.text(errorMessage), new ToolMetadata(duration, provider, Map.of()));
    }

    public static ToolResult denied(ToolInvocationId id, String reason) {
        return new ToolResult(id, ToolResultStatus.DENIED, ToolOutput.text(reason), ToolMetadata.empty());
    }

    public static ToolResult requiresApproval(ToolInvocationId id, String reason) {
        return new ToolResult(id, ToolResultStatus.REQUIRES_APPROVAL, ToolOutput.text(reason), ToolMetadata.empty());
    }

    public static ToolResult timeout(ToolInvocationId id, Duration timeout) {
        return new ToolResult(id, ToolResultStatus.TIMEOUT, ToolOutput.text("Tool execution timed out after " + timeout), ToolMetadata.empty());
    }

    public static ToolResult canceled(ToolInvocationId id) {
        return new ToolResult(id, ToolResultStatus.CANCELED, ToolOutput.text("Tool execution was canceled"), ToolMetadata.empty());
    }
}
