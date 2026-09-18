package tech.kayys.wayang.harness.tool;

import java.util.Objects;

public record SchemaProperty(
        String name,
        SchemaType type,
        String description,
        boolean required
) {
    public SchemaProperty {
        Objects.requireNonNull(name, "Property name cannot be null");
        Objects.requireNonNull(type, "Property type cannot be null");
        if (description == null) {
            description = "";
        }
    }

    public static SchemaProperty required(String name, SchemaType type, String description) {
        return new SchemaProperty(name, type, description, true);
    }

    public static SchemaProperty optional(String name, SchemaType type, String description) {
        return new SchemaProperty(name, type, description, false);
    }
}
