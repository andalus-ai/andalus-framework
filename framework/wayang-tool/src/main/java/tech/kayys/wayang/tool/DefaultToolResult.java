package tech.kayys.wayang.tool;

import tech.kayys.wayang.extension.Metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public record DefaultToolResult(
        ToolInvocationId invocationId,
        ToolResultStatus status,
        ToolOutput output,
        ToolMetadata toolMetadata,
        Map<String, Object> outputs,
        String errorMessage
) implements ToolResult {

    public DefaultToolResult {
        Objects.requireNonNull(invocationId, "invocationId cannot be null");
        Objects.requireNonNull(status, "status cannot be null");
        if (output == null) {
            output = ToolOutput.empty();
        }
        if (toolMetadata == null) {
            toolMetadata = ToolMetadata.empty();
        }
        outputs = outputs != null ? Map.copyOf(outputs) : Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    public DefaultToolResult(ToolInvocationId invocationId, ToolResultStatus status, ToolOutput output, ToolMetadata toolMetadata) {
        this(
                invocationId,
                status,
                output,
                toolMetadata,
                output != null && output.payload() instanceof Map map ? (Map<String, Object>) map : Collections.emptyMap(),
                status == ToolResultStatus.SUCCESS ? null : (output != null ? output.preview() : "")
        );
    }

    @Override
    public Map<String, Object> getOutputs() {
        return outputs;
    }

    @Override
    public boolean isSuccess() {
        return status == ToolResultStatus.SUCCESS;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public Metadata metadata() {
        return Metadata.builder()
                .name("tool-result")
                .description("Result of tool execution: " + status)
                .label("status", status.name())
                .build();
    }
}
