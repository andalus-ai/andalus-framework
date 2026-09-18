package tech.kayys.wayang.harness.tool;

import java.util.Map;
import java.util.Set;

public interface ToolArguments {

    Object value(String name);

    Set<String> names();

    Map<String, Object> asMap();

    default <T> T valueAs(String name, Class<T> type) {
        Object val = value(name);
        if (val == null) {
            return null;
        }
        if (type.isInstance(val)) {
            return type.cast(val);
        }
        throw new IllegalArgumentException("Argument '" + name + "' is not of type " + type.getName() + " (was " + val.getClass().getName() + ")");
    }

    static ToolArguments of(Map<String, Object> map) {
        return new DefaultToolArguments(map);
    }

    static ToolArguments empty() {
        return DefaultToolArguments.EMPTY;
    }
}
