package tech.kayys.andalus.harness.planning;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * An observed outcome for a step in a plan, supplied to a planner during revision.
 */
public record PlanExecutionFeedback(
        PlanStepId stepId,
        Outcome outcome,
        String reason,
        Instant observedAt,
        Map<String, Object> attributes
) {
    /** Observed terminal state of a plan step at the time feedback is produced. */
    public enum Outcome {
        SUCCEEDED,
        FAILED,
        BLOCKED,
        SKIPPED
    }

    public PlanExecutionFeedback {
        Objects.requireNonNull(stepId, "stepId is required");
        Objects.requireNonNull(outcome, "outcome is required");
        reason = reason == null ? "" : reason;
        observedAt = observedAt == null ? Instant.now() : observedAt;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
