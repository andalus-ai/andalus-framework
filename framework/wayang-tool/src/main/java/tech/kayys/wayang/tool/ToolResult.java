package tech.kayys.wayang.tool;

import tech.kayys.wayang.extension.Extension;
import tech.kayys.wayang.extension.Id;
import tech.kayys.wayang.extension.Metadata;
import tech.kayys.wayang.identity.ResourceId;
import tech.kayys.wayang.resource.ResourceType;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Universal contract for the result of a tool execution.
 */
public interface ToolResult extends Extension {

    Map<String, Object> getOutputs();

    boolean isSuccess();

    String getErrorMessage();

    default ToolInvocationId invocationId() {
        return ToolInvocationId.generate();
    }

    default ToolResultStatus status() {
        return isSuccess() ? ToolResultStatus.SUCCESS : ToolResultStatus.FAILURE;
    }

    default ToolOutput output() {
        if (!isSuccess()) {
            return ToolOutput.text(getErrorMessage() != null ? getErrorMessage() : "");
        }
        return ToolOutput.of(getOutputs());
    }

    default ToolMetadata toolMetadata() {
        return ToolMetadata.empty();
    }

    @Override
    default ResourceId id() {
        return new ResourceId.ToolId(Id.fromUUID(UUID.randomUUID()));
    }

    @Override
    default ResourceType type() {
        return new ResourceType.Tool();
    }

    @Override
    default Metadata metadata() {
        return Metadata.builder()
                .name("tool-result")
                .description("Result of tool execution")
                .label("status", status().name())
                .build();
    }

    @SuppressWarnings("unchecked")
    static ToolResult success(ToolInvocationId id, Object payload, Duration duration, String provider) {
        ToolOutput out = payload instanceof String str ? ToolOutput.text(str) : new ToolOutput(payload, 0, String.valueOf(payload), Optional.empty(), false);
        Map<String, Object> outputs = payload instanceof Map ? (Map<String, Object>) payload : Map.of("result", String.valueOf(payload));
        return new DefaultToolResult(id, ToolResultStatus.SUCCESS, out, new ToolMetadata(duration, provider, Map.of()), outputs, null);
    }

    static ToolResult failure(ToolInvocationId id, String errorMessage, Duration duration, String provider) {
        return new DefaultToolResult(id, ToolResultStatus.FAILURE, ToolOutput.text(errorMessage), new ToolMetadata(duration, provider, Map.of()), Collections.emptyMap(), errorMessage);
    }

    static ToolResult denied(ToolInvocationId id, String reason) {
        return new DefaultToolResult(id, ToolResultStatus.DENIED, ToolOutput.text(reason), ToolMetadata.empty(), Collections.emptyMap(), reason);
    }

    static ToolResult requiresApproval(ToolInvocationId id, String reason) {
        return new DefaultToolResult(id, ToolResultStatus.REQUIRES_APPROVAL, ToolOutput.text(reason), ToolMetadata.empty(), Collections.emptyMap(), reason);
    }

    static ToolResult timeout(ToolInvocationId id, Duration timeout) {
        String msg = "Tool execution timed out after " + timeout;
        return new DefaultToolResult(id, ToolResultStatus.TIMEOUT, ToolOutput.text(msg), ToolMetadata.empty(), Collections.emptyMap(), msg);
    }

    static ToolResult canceled(ToolInvocationId id) {
        String msg = "Tool execution was canceled";
        return new DefaultToolResult(id, ToolResultStatus.CANCELED, ToolOutput.text(msg), ToolMetadata.empty(), Collections.emptyMap(), msg);
    }
}
