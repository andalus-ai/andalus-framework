package tech.kayys.andalus.harness.inference;

import java.util.Map;
import java.util.Objects;

/**
 * Payload content and invocation parameters for an inference execution.
 */
public record InferenceInput(
        Object content,
        Map<String, Object> parameters,
        Map<String, Object> attributes
) {
    public InferenceInput {
        Objects.requireNonNull(content, "content is required");
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static InferenceInput of(Object content) {
        return new InferenceInput(content, Map.of(), Map.of());
    }

    public static InferenceInput of(Object content, Map<String, Object> parameters) {
        return new InferenceInput(content, parameters, Map.of());
    }
}
