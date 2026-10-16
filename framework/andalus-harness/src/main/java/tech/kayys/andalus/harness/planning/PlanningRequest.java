package tech.kayys.andalus.harness.planning;

import tech.kayys.andalus.harness.protocol.DecisionContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Request payload for creating an agent plan given a turn-scoped {@link DecisionContext}.
 */
public record PlanningRequest(
        DecisionContext context,
        Optional<AgentPlan> previousPlan,
        Map<String, Object> attributes
) {
    public PlanningRequest {
        Objects.requireNonNull(context, "context is required");
        previousPlan = previousPlan == null ? Optional.empty() : previousPlan;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static PlanningRequest of(DecisionContext context) {
        return new PlanningRequest(context, Optional.empty(), Map.of());
    }
}
