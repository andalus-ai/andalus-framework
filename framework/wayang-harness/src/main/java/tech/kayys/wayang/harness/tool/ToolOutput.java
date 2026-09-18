package tech.kayys.wayang.harness.tool;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Represents a tool output.
 *
 * <p>Its components capture `payload`, `size bytes`, `preview`, `artifact ref`, `truncated`.</p>
 *
 * @param payload the payload
 * @param sizeBytes the size bytes
 * @param preview the preview
 * @param artifactRef the artifact ref
 * @param truncated the truncated
 */


public record ToolOutput(
        Object payload,
        long sizeBytes,
        String preview,
        Optional<String> artifactRef,
        boolean truncated
) {
    public ToolOutput {
        if (artifactRef == null) {
            artifactRef = Optional.empty();
        }
    }

    public static ToolOutput text(String content) {
        if (content == null) {
            return new ToolOutput(null, 0, "", Optional.empty(), false);
        }
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return new ToolOutput(content, bytes.length, content.length() > 200 ? content.substring(0, 200) + "..." : content, Optional.empty(), false);
    }

    public static ToolOutput of(Object payload, long sizeBytes, String preview, Optional<String> artifactRef, boolean truncated) {
        return new ToolOutput(payload, sizeBytes, preview, artifactRef, truncated);
    }

    public static ToolOutput empty() {
        return new ToolOutput(null, 0, "", Optional.empty(), false);
    }
}
