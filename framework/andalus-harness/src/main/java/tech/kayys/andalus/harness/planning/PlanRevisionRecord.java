package tech.kayys.andalus.harness.planning;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Immutable audit record for one accepted plan revision.
 *
 * <p>The before/after plans and their calculated structural diff are snapshots.
 * Persistence and retention are delegated to a {@link PlanRevisionHistory}
 * implementation.</p>
 */
public record PlanRevisionRecord(
        PlanId planId,
        long fromRevision,
        long toRevision,
        String reason,
        AgentPlan previousPlan,
        AgentPlan revisedPlan,
        List<PlanExecutionFeedback> feedback,
        Instant recordedAt,
        PlanRevisionPolicy revisionPolicy,
        PlanRevisionDiff diff
) {
    public PlanRevisionRecord {
        Objects.requireNonNull(planId, "planId is required");
        Objects.requireNonNull(previousPlan, "previousPlan is required");
        Objects.requireNonNull(revisedPlan, "revisedPlan is required");
        if (fromRevision < 0 || fromRevision == Long.MAX_VALUE || toRevision != fromRevision + 1) {
            throw new IllegalArgumentException("revision record must describe exactly one forward revision");
        }
        if (!planId.equals(previousPlan.id()) || !planId.equals(revisedPlan.id())) {
            throw new IllegalArgumentException("revision record plans must share the recorded plan ID");
        }
        if (previousPlan.revision() != fromRevision || revisedPlan.revision() != toRevision) {
            throw new IllegalArgumentException("revision record numbers must match the plan snapshots");
        }
        reason = reason == null ? "" : reason;
        feedback = feedback == null ? List.of() : List.copyOf(feedback);
        recordedAt = recordedAt == null ? Instant.now() : recordedAt;
        Objects.requireNonNull(revisionPolicy, "revisionPolicy is required");
        Objects.requireNonNull(diff, "diff is required");
    }

    /** Backwards-compatible constructor using strict completed-step preservation. */
    public PlanRevisionRecord(
            PlanId planId,
            long fromRevision,
            long toRevision,
            String reason,
            AgentPlan previousPlan,
            AgentPlan revisedPlan,
            List<PlanExecutionFeedback> feedback,
            Instant recordedAt
    ) {
        this(planId, fromRevision, toRevision, reason, previousPlan, revisedPlan,
                feedback, recordedAt, PlanRevisionPolicy.PRESERVE_COMPLETED_STEPS,
                PlanRevisionDiffCalculator.calculate(previousPlan, revisedPlan, feedback));
    }

    /** Backwards-compatible constructor retaining an explicitly selected policy. */
    public PlanRevisionRecord(
            PlanId planId,
            long fromRevision,
            long toRevision,
            String reason,
            AgentPlan previousPlan,
            AgentPlan revisedPlan,
            List<PlanExecutionFeedback> feedback,
            Instant recordedAt,
            PlanRevisionPolicy revisionPolicy
    ) {
        this(planId, fromRevision, toRevision, reason, previousPlan, revisedPlan,
                feedback, recordedAt, revisionPolicy,
                PlanRevisionDiffCalculator.calculate(previousPlan, revisedPlan, feedback));
    }

    public static PlanRevisionRecord of(
            AgentPlan previousPlan,
            AgentPlan revisedPlan,
            String reason,
            List<PlanExecutionFeedback> feedback
    ) {
        return of(previousPlan, revisedPlan, reason, feedback,
                PlanRevisionPolicy.PRESERVE_COMPLETED_STEPS);
    }

    public static PlanRevisionRecord of(
            AgentPlan previousPlan,
            AgentPlan revisedPlan,
            String reason,
            List<PlanExecutionFeedback> feedback,
            PlanRevisionPolicy revisionPolicy
    ) {
        return new PlanRevisionRecord(
                previousPlan.id(), previousPlan.revision(), revisedPlan.revision(),
                reason, previousPlan, revisedPlan, feedback, Instant.now(), revisionPolicy,
                PlanRevisionDiffCalculator.calculate(previousPlan, revisedPlan, feedback));
    }
}
