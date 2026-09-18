package tech.kayys.wayang.tool;

import java.time.Duration;
import java.util.Map;

/**
 * Execution metadata captured during tool invocation.
 */
public record ToolMetadata(
        Duration duration,
        String provider,
        Map<String, Object> attributes
) {
    public ToolMetadata {
        if (duration == null) {
            duration = Duration.ZERO;
        }
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public static ToolMetadata of(Duration duration, String provider) {
        return new ToolMetadata(duration, provider, Map.of());
    }

    public static ToolMetadata empty() {
        return new ToolMetadata(Duration.ZERO, "unknown", Map.of());
    }
}
