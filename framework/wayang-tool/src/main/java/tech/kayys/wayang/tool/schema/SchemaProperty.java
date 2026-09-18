package tech.kayys.wayang.tool.schema;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Describes an individual property within a tool schema.
 */
public record SchemaProperty(
        SchemaType type,
        String description,
        boolean required,
        SchemaProperty items,
        Map<String, SchemaProperty> properties
) {
    public SchemaProperty {
        Objects.requireNonNull(type, "SchemaType cannot be null");
        properties = properties != null ? Map.copyOf(properties) : Collections.emptyMap();
    }

    public static SchemaProperty string(String description, boolean required) {
        return new SchemaProperty(SchemaType.STRING, description, required, null, Map.of());
    }

    public static SchemaProperty integer(String description, boolean required) {
        return new SchemaProperty(SchemaType.INTEGER, description, required, null, Map.of());
    }

    public static SchemaProperty booleanType(String description, boolean required) {
        return new SchemaProperty(SchemaType.BOOLEAN, description, required, null, Map.of());
    }

    public static SchemaProperty required(String name, SchemaType type, String description) {
        return new SchemaProperty(type, description, true, null, Map.of());
    }

    public static SchemaProperty optional(String name, SchemaType type, String description) {
        return new SchemaProperty(type, description, false, null, Map.of());
    }
}
