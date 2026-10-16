package tech.kayys.andalus.harness.inference;

import java.util.Map;
import java.util.Objects;

/**
 * Concrete request passed to {@link InferenceRuntime} containing the plan and input payload.
 */
public record InferenceRequest(
        InferencePlan plan,
        InferenceInput input,
        Map<String, Object> attributes
) {
    public InferenceRequest {
        Objects.requireNonNull(plan, "plan is required");
        Objects.requireNonNull(input, "input is required");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static InferenceRequest of(InferencePlan plan, InferenceInput input) {
        return new InferenceRequest(plan, input, Map.of());
    }

    public static InferenceRequest of(InferencePlan plan, Object content) {
        return new InferenceRequest(plan, InferenceInput.of(content), Map.of());
    }
}
