package tech.kayys.andalus.harness.planning;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Immutable in-plan audit entry describing one accepted plan revision.
 *
 * <p>The entry is carried in {@code AgentPlan.metadata} under
 * {@code revision_history}. It is an audit value, not a persistence mechanism;
 * runtimes remain responsible for storing plan snapshots durably.</p>
 */
public record PlanRevisionHistoryEntry(
        long fromRevision,
        long toRevision,
        String reason,
        Instant revisedAt,
        List<PlanStepId> previousStepIds,
        List<PlanStepId> revisedStepIds
) {
    public PlanRevisionHistoryEntry {
        if (fromRevision < 0 || fromRevision == Long.MAX_VALUE) {
            throw new IllegalArgumentException("fromRevision must be non-negative and incrementable");
        }
        if (toRevision != fromRevision + 1) {
            throw new IllegalArgumentException("toRevision must equal fromRevision + 1");
        }
        reason = reason == null ? "" : reason;
        Objects.requireNonNull(revisedAt, "revisedAt is required");
        previousStepIds = previousStepIds == null ? List.of() : List.copyOf(previousStepIds);
        revisedStepIds = revisedStepIds == null ? List.of() : List.copyOf(revisedStepIds);
    }
}
