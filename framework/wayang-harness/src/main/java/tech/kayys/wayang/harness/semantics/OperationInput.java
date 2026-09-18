package tech.kayys.wayang.harness.semantics;

import java.util.Map;

/**
 * Encapsulates the runtime payload and parameters of an operation.
 */
public record OperationInput(Map<String, Object> payload) {

    public OperationInput {
        payload = payload != null ? Map.copyOf(payload) : Map.of();
    }

    public static OperationInput empty() {
        return new OperationInput(Map.of());
    }

    public static OperationInput of(Map<String, Object> payload) {
        return new OperationInput(payload);
    }
}
