package tech.kayys.andalus.harness.inference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Describes what inference capability and constraints an agent or task requires.
 */
public record InferenceRequirement(
        InferenceTask task,
        Set<String> capabilities,
        Optional<String> requestedModel,
        Optional<String> requestedProvider,
        InferenceInputRequirements input,
        InferenceOutputRequirements output,
        InferenceConstraints constraints,
        Map<String, Object> attributes
) {
    public InferenceRequirement {
        Objects.requireNonNull(task, "task is required");
        capabilities = capabilities == null ? Set.of() : Set.copyOf(capabilities);
        requestedModel = requestedModel == null ? Optional.empty() : requestedModel;
        requestedProvider = requestedProvider == null ? Optional.empty() : requestedProvider;
        input = input == null ? InferenceInputRequirements.empty() : input;
        output = output == null ? InferenceOutputRequirements.text() : output;
        constraints = constraints == null ? InferenceConstraints.defaults() : constraints;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static InferenceRequirement forGeneration(String requestedModel) {
        return new InferenceRequirement(
                InferenceTask.GENERATION,
                Set.of("text-generation"),
                Optional.ofNullable(requestedModel),
                Optional.empty(),
                InferenceInputRequirements.empty(),
                InferenceOutputRequirements.text(),
                InferenceConstraints.defaults(),
                Map.of()
        );
    }

    public static InferenceRequirement forReasoning(Set<String> capabilities) {
        return new InferenceRequirement(
                InferenceTask.REASONING,
                capabilities,
                Optional.empty(),
                Optional.empty(),
                InferenceInputRequirements.empty(),
                InferenceOutputRequirements.text(),
                InferenceConstraints.defaults(),
                Map.of()
        );
    }
}
