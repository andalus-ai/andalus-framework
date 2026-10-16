package tech.kayys.andalus.harness.planning;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Proposed, versioned plan representing a sequence of steps toward a goal.
 */
public record AgentPlan(
        PlanId id,
        String executionId,
        Optional<String> goalId,
        long revision,
        PlanStatus status,
        List<PlanStep> steps,
        Instant createdAt,
        Map<String, Object> metadata
) {
    public AgentPlan {
        Objects.requireNonNull(id, "id is required");
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId is required");
        }
        if (revision < 0) {
            throw new IllegalArgumentException("revision must be non-negative");
        }
        Objects.requireNonNull(status, "status is required");
        createdAt = createdAt == null ? Instant.now() : createdAt;
        goalId = goalId == null ? Optional.empty() : goalId;
        steps = steps == null ? List.of() : List.copyOf(steps);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public AgentPlan revise(List<PlanStep> newSteps, String reason) {
        Map<String, Object> newMetadata = new HashMap<>(metadata);
        if (reason != null && !reason.isBlank()) {
            newMetadata.put("revision_reason", reason);
        }
        newMetadata.put("previous_revision", revision);
        return new AgentPlan(
                id,
                executionId,
                goalId,
                revision + 1,
                status,
                newSteps,
                createdAt,
                newMetadata
        );
    }
}
