package tech.kayys.wayang.execution.policy;

import java.util.Map;
import java.util.Objects;

/**
 * Declares an attempted sandbox operation subject to unified policy evaluation.
 */
public record SandboxOperation(
        String operationType,
        String target,
        Map<String, Object> parameters
) {

    public SandboxOperation {
        Objects.requireNonNull(operationType, "operationType cannot be null");
        target = target != null ? target : "";
        parameters = parameters != null ? Map.copyOf(parameters) : Map.of();
    }

    public static SandboxOperation of(String type, String target) {
        return new SandboxOperation(type, target, Map.of());
    }
}
