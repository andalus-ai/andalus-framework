package tech.kayys.wayang.tool.schema;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record DefaultToolInputSchema(
        SchemaType rootType,
        Map<String, SchemaProperty> properties,
        Set<String> required
) implements ToolInputSchema {

    public DefaultToolInputSchema {
        if (rootType == null) {
            rootType = SchemaType.OBJECT;
        }
        properties = properties != null ? Map.copyOf(properties) : Collections.emptyMap();
        required = required != null ? Set.copyOf(required) : Collections.emptySet();
    }

    public static DefaultToolInputSchema empty() {
        return new DefaultToolInputSchema(SchemaType.OBJECT, Map.of(), Set.of());
    }

    public static DefaultToolInputSchema of(Map<String, SchemaProperty> properties, Set<String> required) {
        return new DefaultToolInputSchema(SchemaType.OBJECT, properties, required);
    }

    public static DefaultToolInputSchema of(Map<String, SchemaProperty> properties) {
        Set<String> req = properties != null
                ? properties.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().required())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet())
                : Collections.emptySet();
        return new DefaultToolInputSchema(SchemaType.OBJECT, properties, req);
    }
}
