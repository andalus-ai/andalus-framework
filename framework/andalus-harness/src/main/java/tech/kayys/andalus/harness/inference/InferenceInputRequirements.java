package tech.kayys.andalus.harness.inference;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Requirements on the input representation for an inference operation.
 */
public record InferenceInputRequirements(
        Optional<Integer> estimatedTokens,
        boolean multimodal,
        Set<String> modalities,
        Map<String, Object> attributes
) {
    public InferenceInputRequirements {
        estimatedTokens = estimatedTokens == null ? Optional.empty() : estimatedTokens;
        modalities = modalities == null ? Set.of() : Set.copyOf(modalities);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static InferenceInputRequirements standard(int estimatedTokens) {
        return new InferenceInputRequirements(Optional.of(estimatedTokens), false, Set.of("text"), Map.of());
    }

    public static InferenceInputRequirements empty() {
        return new InferenceInputRequirements(Optional.empty(), false, Set.of(), Map.of());
    }
}
