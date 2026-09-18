package tech.kayys.wayang.harness.tool;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Provides the default implementation of the tool output validator contract.
 */


public class DefaultToolOutputValidator implements ToolOutputValidator {

    public static final long DEFAULT_MAX_INLINE_BYTES = 64 * 1024; // 64 KB

    private final long maxInlineBytes;

    public DefaultToolOutputValidator(long maxInlineBytes) {
        this.maxInlineBytes = maxInlineBytes;
    }

    public DefaultToolOutputValidator() {
        this(DEFAULT_MAX_INLINE_BYTES);
    }

    @Override
    public ToolResult validate(ToolResult result, ToolExecutionContext context) {
        if (result == null) {
            return ToolResult.failure(ToolInvocationId.generate(), "Null tool result", null, "validator");
        }
        if (!result.isSuccess() || result.output() == null) {
            return result;
        }

        ToolOutput output = result.output();
        if (output.payload() == null) {
            return result;
        }

        byte[] rawBytes;
        if (output.payload() instanceof byte[] bytes) {
            rawBytes = bytes;
        } else if (output.payload() instanceof String str) {
            rawBytes = str.getBytes(StandardCharsets.UTF_8);
        } else {
            rawBytes = String.valueOf(output.payload()).getBytes(StandardCharsets.UTF_8);
        }

        if (rawBytes.length > maxInlineBytes && context != null && context.artifacts() != null) {
            String artifactId = context.artifacts().store("tool-output-" + result.invocationId().value(), rawBytes, "application/octet-stream");
            String preview = rawBytes.length > 200 ? new String(rawBytes, 0, 200, StandardCharsets.UTF_8) + "... [truncated, stored in artifact: " + artifactId + "]" : new String(rawBytes, StandardCharsets.UTF_8);
            ToolOutput newOutput = new ToolOutput(preview, rawBytes.length, preview, Optional.of(artifactId), true);
            return new ToolResult(result.invocationId(), result.status(), newOutput, result.metadata());
        }

        return result;
    }
}
