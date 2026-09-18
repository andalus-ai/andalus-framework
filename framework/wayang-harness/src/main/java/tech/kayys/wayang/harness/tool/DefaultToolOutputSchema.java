package tech.kayys.wayang.harness.tool;

import java.util.Map;
import java.util.Objects;

public record DefaultToolOutputSchema(
        SchemaType rootType,
        Map<String, SchemaProperty> properties
) implements ToolOutputSchema {

    public DefaultToolOutputSchema {
        Objects.requireNonNull(rootType, "rootType cannot be null");
        properties = properties != null ? Map.copyOf(properties) : Map.of();
    }

    public static DefaultToolOutputSchema of(Map<String, SchemaProperty> properties) {
        return new DefaultToolOutputSchema(SchemaType.OBJECT, properties);
    }

    public static DefaultToolOutputSchema text() {
        return new DefaultToolOutputSchema(SchemaType.STRING, Map.of());
    }
}
