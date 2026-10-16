package tech.kayys.andalus.harness.planning;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a single proposed step within an agent plan.
 */
public record PlanStep(
        PlanStepId id,
        String description,
        Optional<String> taskId,
        List<PlanStepId> dependsOn,
        String expectedOutcome,
        List<String> completionCriteria,
        Map<String, Object> metadata
) {
    public PlanStep {
        Objects.requireNonNull(id, "id is required");
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description is required");
        }
        taskId = taskId == null ? Optional.empty() : taskId;
        dependsOn = dependsOn == null ? List.of() : List.copyOf(dependsOn);
        expectedOutcome = expectedOutcome == null ? "" : expectedOutcome;
        completionCriteria = completionCriteria == null ? List.of() : List.copyOf(completionCriteria);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static PlanStep of(String description) {
        return new PlanStep(
                PlanStepId.generate(),
                description,
                Optional.empty(),
                List.of(),
                "",
                List.of(),
                Map.of()
        );
    }

    public static PlanStep of(String description, List<PlanStepId> dependsOn) {
        return new PlanStep(
                PlanStepId.generate(),
                description,
                Optional.empty(),
                dependsOn,
                "",
                List.of(),
                Map.of()
        );
    }
}
