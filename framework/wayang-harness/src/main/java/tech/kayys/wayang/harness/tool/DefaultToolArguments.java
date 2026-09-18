package tech.kayys.wayang.harness.tool;

import java.util.Map;
import java.util.Set;

public final class DefaultToolArguments implements ToolArguments {

    public static final DefaultToolArguments EMPTY = new DefaultToolArguments(Map.of());

    private final Map<String, Object> values;

    public DefaultToolArguments(Map<String, Object> values) {
        this.values = values != null ? Map.copyOf(values) : Map.of();
    }

    @Override
    public Object value(String name) {
        return values.get(name);
    }

    @Override
    public Set<String> names() {
        return values.keySet();
    }

    @Override
    public Map<String, Object> asMap() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ToolArguments other)) return false;
        return values.equals(other.asMap());
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return "ToolArguments" + values;
    }
}
