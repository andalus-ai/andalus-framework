package tech.kayys.wayang.harness.tool;

import java.util.*;

public record DefaultToolInputSchema(
        SchemaType rootType,
        Map<String, SchemaProperty> properties,
        Set<String> required
) implements ToolInputSchema {

    public DefaultToolInputSchema {
        Objects.requireNonNull(rootType, "rootType cannot be null");
        properties = properties != null ? Map.copyOf(properties) : Map.of();
        required = required != null ? Set.copyOf(required) : Set.of();
    }

    public static DefaultToolInputSchema of(Map<String, SchemaProperty> properties) {
        Set<String> req = new HashSet<>();
        if (properties != null) {
            properties.forEach((name, prop) -> {
                if (prop.required()) {
                    req.add(name);
                }
            });
        }
        return new DefaultToolInputSchema(SchemaType.OBJECT, properties, req);
    }

    public static DefaultToolInputSchema empty() {
        return new DefaultToolInputSchema(SchemaType.OBJECT, Map.of(), Set.of());
    }
}
