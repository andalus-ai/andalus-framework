package tech.kayys.wayang.execution.governance.limits;

import tech.kayys.wayang.execution.governance.PolicyEvaluationContext;

import java.util.Map;
import java.util.Objects;

public record LimitContext(
        PolicyEvaluationContext policyContext,
        String toolName,
        String capabilityId,
        String providerId,
        Map<String, String> dimensions
) {

    public LimitContext {
        Objects.requireNonNull(
                policyContext,
                "policyContext cannot be null"
        );

        toolName = normalize(toolName);
        capabilityId = normalize(capabilityId);
        providerId = normalize(providerId);

        dimensions = dimensions == null
                ? Map.of()
                : Map.copyOf(dimensions);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
