package tech.kayys.wayang.tool;

import java.util.Map;

/**
 * Encapsulates arguments supplied for a tool invocation.
 */
public interface ToolArguments {

    Object value(String key);

    Map<String, Object> asMap();

    default boolean has(String key) {
        return asMap().containsKey(key);
    }

    default <T> T get(String key, Class<T> type) {
        Object val = value(key);
        if (val == null) {
            return null;
        }
        return type.cast(val);
    }

    static ToolArguments of(Map<String, Object> map) {
        return new DefaultToolArguments(map);
    }

    static ToolArguments empty() {
        return new DefaultToolArguments(Map.of());
    }
}
