package tech.kayys.andalus.harness.inference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Concrete decision plan specifying how an {@link InferenceRequirement} will be fulfilled.
 */
public record InferencePlan(
        String id,
        String requirementId,
        String modelId,
        Optional<String> providerId,
        Set<String> matchedCapabilities,
        InferenceConstraints constraints,
        String strategy,
        Map<String, Object> metadata
) {
    public InferencePlan {
        id = id == null || id.isBlank() ? UUID.randomUUID().toString() : id;
        requirementId = requirementId == null ? "" : requirementId;
        Objects.requireNonNull(modelId, "modelId is required");
        providerId = providerId == null ? Optional.empty() : providerId;
        matchedCapabilities = matchedCapabilities == null ? Set.of() : Set.copyOf(matchedCapabilities);
        constraints = constraints == null ? InferenceConstraints.defaults() : constraints;
        strategy = strategy == null || strategy.isBlank() ? "DIRECT" : strategy;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static InferencePlan direct(String modelId, Set<String> capabilities) {
        return new InferencePlan(
                UUID.randomUUID().toString(),
                "",
                modelId,
                Optional.empty(),
                capabilities,
                InferenceConstraints.defaults(),
                "DIRECT",
                Map.of()
        );
    }

    public static InferencePlan direct(String modelId, String providerId, Set<String> capabilities) {
        return new InferencePlan(
                UUID.randomUUID().toString(),
                "",
                modelId,
                Optional.ofNullable(providerId),
                capabilities,
                InferenceConstraints.defaults(),
                "DIRECT",
                Map.of()
        );
    }
}
