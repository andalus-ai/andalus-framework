package tech.kayys.wayang.workflow.graph;

import java.util.Map;

/**
 * Metadata associated with an execution graph.
 */
public record GraphMetadata(
        String name,
        String description,
        Map<String, Object> tags
) {

    public GraphMetadata {
        name = name != null ? name : "";
        description = description != null ? description : "";
        tags = tags != null ? Map.copyOf(tags) : Map.of();
    }

    public static GraphMetadata of(String name) {
        return new GraphMetadata(name, "", Map.of());
    }

    public static GraphMetadata of(String name, String description) {
        return new GraphMetadata(name, description, Map.of());
    }
}
