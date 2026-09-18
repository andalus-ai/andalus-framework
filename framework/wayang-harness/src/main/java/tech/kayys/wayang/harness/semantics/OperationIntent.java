package tech.kayys.wayang.harness.semantics;

import java.util.Map;
import java.util.Objects;

/**
 * Declares the semantic intent behind an operation.
 */
public record OperationIntent(String summary, Map<String, Object> parameters) {

    public OperationIntent {
        Objects.requireNonNull(summary, "Operation summary cannot be null");
        parameters = parameters != null ? Map.copyOf(parameters) : Map.of();
    }

    public static OperationIntent of(String summary) {
        return new OperationIntent(summary, Map.of());
    }

    public static OperationIntent of(String summary, Map<String, Object> parameters) {
        return new OperationIntent(summary, parameters);
    }
}
