package tech.kayys.wayang.tool.resolution;

import java.util.Map;

public record ToolRequestContext(
        Object identity,
        Object resources,
        Map<String, Object> metadata
) {
    public ToolRequestContext {
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static ToolRequestContext of(Object identity, Object resources) {
        return new ToolRequestContext(identity, resources, Map.of());
    }

    public static ToolRequestContext of(Object identity, Object resources, Map<String, Object> metadata) {
        return new ToolRequestContext(identity, resources, metadata);
    }
}
