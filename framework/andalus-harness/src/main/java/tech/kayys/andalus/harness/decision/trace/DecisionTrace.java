package tech.kayys.andalus.harness.decision.trace;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An immutable trace recording the observable lineage of a decision.
 */
public record DecisionTrace(
        String id,
        String executionId,
        String turnId,
        String traceId,
        long sequence,
        Optional<String> contextSnapshotReference,
        Optional<String> stateRevision,
        Optional<String> goalId,
        Optional<String> taskId,
        String strategyId,
        DecisionTraceStatus status,
        List<DecisionTraceEntry> entries,
        Instant startedAt,
        Optional<Instant> completedAt,
        Map<String, Object> metadata
) {
    public DecisionTrace {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId is required");
        }
        if (sequence < 0) {
            throw new IllegalArgumentException("sequence must be non-negative");
        }
        if (status == null) {
            status = DecisionTraceStatus.IN_PROGRESS;
        }
        if (startedAt == null) {
            startedAt = Instant.now();
        }

        turnId = turnId == null ? "" : turnId;
        traceId = traceId == null ? "" : traceId;
        strategyId = strategyId == null ? "" : strategyId;
        contextSnapshotReference = contextSnapshotReference == null
                ? Optional.empty() : contextSnapshotReference;
        stateRevision = stateRevision == null
                ? Optional.empty() : stateRevision;
        goalId = goalId == null ? Optional.empty() : goalId;
        taskId = taskId == null ? Optional.empty() : taskId;
        entries = entries == null ? List.of() : List.copyOf(entries);
        completedAt = completedAt == null ? Optional.empty() : completedAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public DecisionTrace withEntry(DecisionTraceEntry entry) {
        if (entry == null) {
            return this;
        }
        List<DecisionTraceEntry> updatedEntries = new ArrayList<>(entries);
        updatedEntries.add(entry);
        return new DecisionTrace(
                id,
                executionId,
                turnId,
                traceId,
                sequence + 1,
                contextSnapshotReference,
                stateRevision,
                goalId,
                taskId,
                strategyId,
                status,
                updatedEntries,
                startedAt,
                completedAt,
                metadata
        );
    }

    public DecisionTrace complete(DecisionTraceStatus finalStatus, Optional<String> outcomeReference, Map<String, Object> additionalMetadata) {
        Map<String, Object> updatedMetadata = new HashMap<>(metadata);
        if (additionalMetadata != null) {
            updatedMetadata.putAll(additionalMetadata);
        }
        outcomeReference.ifPresent(ref -> updatedMetadata.put("outcomeReference", ref));

        return new DecisionTrace(
                id,
                executionId,
                turnId,
                traceId,
                sequence,
                contextSnapshotReference,
                stateRevision,
                goalId,
                taskId,
                strategyId,
                finalStatus == null ? DecisionTraceStatus.COMPLETED : finalStatus,
                entries,
                startedAt,
                Optional.of(Instant.now()),
                updatedMetadata
        );
    }
}
