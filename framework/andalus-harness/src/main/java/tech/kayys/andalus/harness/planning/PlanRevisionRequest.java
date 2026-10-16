package tech.kayys.andalus.harness.planning;

import tech.kayys.andalus.harness.protocol.DecisionContext;

import java.util.Map;
import java.util.Objects;

/**
 * Request payload for revising an existing plan based on new observations or changed constraints.
 */
public record PlanRevisionRequest(
        AgentPlan currentPlan,
        DecisionContext context,
        String revisionReason,
        Map<String, Object> attributes
) {
    public PlanRevisionRequest {
        Objects.requireNonNull(currentPlan, "currentPlan is required");
        Objects.requireNonNull(context, "context is required");
        revisionReason = revisionReason == null ? "" : revisionReason;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
