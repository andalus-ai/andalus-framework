package tech.kayys.andalus.harness.planning;

import java.time.Instant;
import java.util.ArrayList;
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
        String normalizedReason = reason == null ? "" : reason;
        Object existingHistory = metadata.get("revision_history");
        List<PlanRevisionHistoryEntry> history = new ArrayList<>();
        if (existingHistory instanceof List<?> entries) {
            for (Object entry : entries) {
                if (!(entry instanceof PlanRevisionHistoryEntry historyEntry)) {
                    throw new IllegalStateException("revision_history contains an unsupported entry");
                }
                history.add(historyEntry);
            }
        } else if (existingHistory != null) {
            throw new IllegalStateException("revision_history must be a list");
        }
        history.add(new PlanRevisionHistoryEntry(
                revision, revision + 1, normalizedReason, Instant.now(),
                steps.stream().map(PlanStep::id).toList(),
                (newSteps == null ? List.<PlanStep>of() : newSteps).stream().map(PlanStep::id).toList()));
        newMetadata.put("revision_history", List.copyOf(history));
        if (!normalizedReason.isBlank()) {
            newMetadata.put("revision_reason", normalizedReason);
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
