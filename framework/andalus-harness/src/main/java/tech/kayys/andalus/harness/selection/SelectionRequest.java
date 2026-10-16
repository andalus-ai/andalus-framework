package tech.kayys.andalus.harness.selection;

import tech.kayys.andalus.harness.protocol.DecisionContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Request payload for evaluating and selecting candidate tools and/or skills.
 */
public record SelectionRequest(
        DecisionContext context,
        SelectionTarget target,
        Optional<String> requestedCapability,
        Map<String, Object> inputRequirements,
        Map<String, Object> attributes
) {
    public SelectionRequest {
        Objects.requireNonNull(context, "context is required");
        Objects.requireNonNull(target, "target is required");

        requestedCapability = requestedCapability == null
                ? Optional.empty()
                : requestedCapability;
        inputRequirements = inputRequirements == null
                ? Map.of()
                : Map.copyOf(inputRequirements);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static SelectionRequest of(DecisionContext context, SelectionTarget target) {
        return new SelectionRequest(context, target, Optional.empty(), Map.of(), Map.of());
    }

    public static SelectionRequest forCapability(DecisionContext context, SelectionTarget target, String capability) {
        return new SelectionRequest(context, target, Optional.ofNullable(capability), Map.of(), Map.of());
    }
}
