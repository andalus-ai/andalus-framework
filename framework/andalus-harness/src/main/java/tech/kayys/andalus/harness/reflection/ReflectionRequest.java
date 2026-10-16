package tech.kayys.andalus.harness.reflection;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Request payload initiating a reflection assessment.
 */
public record ReflectionRequest(
        String executionId,
        String turnId,
        ReflectionTrigger trigger,
        Optional<String> goalId,
        Optional<String> taskId,
        List<String> observationReferences,
        Optional<Long> stateRevision,
        Map<String, Object> attributes
) {
    public ReflectionRequest {
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId is required");
        }
        Objects.requireNonNull(trigger, "trigger is required");
        turnId = turnId == null ? "" : turnId;
        goalId = goalId == null ? Optional.empty() : goalId;
        taskId = taskId == null ? Optional.empty() : taskId;
        observationReferences = observationReferences == null ? List.of() : List.copyOf(observationReferences);
        stateRevision = stateRevision == null ? Optional.empty() : stateRevision;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static ReflectionRequest of(String executionId, String turnId, ReflectionTrigger trigger) {
        return new ReflectionRequest(
                executionId,
                turnId,
                trigger,
                Optional.empty(),
                Optional.empty(),
                List.of(),
                Optional.empty(),
                Map.of()
        );
    }

    public static ReflectionRequest of(
            String executionId,
            String turnId,
            ReflectionTrigger trigger,
            List<String> observationReferences
    ) {
        return new ReflectionRequest(
                executionId,
                turnId,
                trigger,
                Optional.empty(),
                Optional.empty(),
                observationReferences,
                Optional.empty(),
                Map.of()
        );
    }
}
