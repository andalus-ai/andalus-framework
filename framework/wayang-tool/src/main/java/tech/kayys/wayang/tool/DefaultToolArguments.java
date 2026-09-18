package tech.kayys.wayang.tool;

import java.util.Collections;
import java.util.Map;

public record DefaultToolArguments(Map<String, Object> values) implements ToolArguments {

    public DefaultToolArguments {
        values = values != null ? Map.copyOf(values) : Collections.emptyMap();
    }

    @Override
    public Object value(String key) {
        return values.get(key);
    }

    @Override
    public Map<String, Object> asMap() {
        return values;
    }

    public static DefaultToolArguments empty() {
        return new DefaultToolArguments(Collections.emptyMap());
    }

    public static DefaultToolArguments of(Map<String, Object> values) {
        return new DefaultToolArguments(values);
    }
}
