package tech.kayys.andalus.harness.inference;

import java.util.Map;
import java.util.Optional;

/**
 * Requirements on the output representation for an inference operation.
 */
public record InferenceOutputRequirements(
        OutputFormat format,
        Optional<String> schema,
        boolean streaming,
        Map<String, Object> attributes
) {
    public InferenceOutputRequirements {
        format = format == null ? OutputFormat.TEXT : format;
        schema = schema == null ? Optional.empty() : schema;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static InferenceOutputRequirements text() {
        return new InferenceOutputRequirements(OutputFormat.TEXT, Optional.empty(), false, Map.of());
    }

    public static InferenceOutputRequirements json(String schema) {
        return new InferenceOutputRequirements(OutputFormat.JSON, Optional.ofNullable(schema), false, Map.of());
    }

    public static InferenceOutputRequirements streamingText() {
        return new InferenceOutputRequirements(OutputFormat.TEXT, Optional.empty(), true, Map.of());
    }
}
