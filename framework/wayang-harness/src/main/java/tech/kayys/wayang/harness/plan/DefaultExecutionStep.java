package tech.kayys.wayang.harness.plan;

import java.util.Map;
import java.util.Objects;

/**
 * Immutable value record implementing {@link ExecutionStep}.
 */
public record DefaultExecutionStep(
        String id,
        String type,
        Map<String, Object> parameters
) implements ExecutionStep {

    public DefaultExecutionStep {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }

    public static DefaultExecutionStep of(String id, String type) {
        return new DefaultExecutionStep(id, type, Map.of());
    }

    public static DefaultExecutionStep of(String id, String type, Map<String, Object> parameters) {
        return new DefaultExecutionStep(id, type, parameters);
    }
}
