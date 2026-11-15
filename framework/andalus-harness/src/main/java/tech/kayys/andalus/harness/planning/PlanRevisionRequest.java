package tech.kayys.andalus.harness.planning;

import tech.kayys.andalus.harness.protocol.DecisionContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Request payload for revising an existing plan based on new observations or changed constraints.
 */
public record PlanRevisionRequest(
        AgentPlan currentPlan,
        DecisionContext context,
        String revisionReason,
        Map<String, Object> attributes,
        List<PlanExecutionFeedback> feedback
) {
    public PlanRevisionRequest {
        Objects.requireNonNull(currentPlan, "currentPlan is required");
        Objects.requireNonNull(context, "context is required");
        revisionReason = revisionReason == null ? "" : revisionReason;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        feedback = feedback == null ? List.of() : List.copyOf(feedback);

        Set<PlanStepId> stepIds = currentPlan.steps().stream()
                .map(PlanStep::id)
                .collect(Collectors.toSet());
        for (PlanExecutionFeedback observation : feedback) {
            Objects.requireNonNull(observation, "feedback entries must not be null");
            if (!stepIds.contains(observation.stepId())) {
                throw new IllegalArgumentException(
                        "Feedback references a step that is not part of the current plan: " + observation.stepId());
            }
        }
    }

    /** Backwards-compatible constructor for revisions without execution feedback. */
    public PlanRevisionRequest(
            AgentPlan currentPlan,
            DecisionContext context,
            String revisionReason,
            Map<String, Object> attributes
    ) {
        this(currentPlan, context, revisionReason, attributes, List.of());
    }
}
