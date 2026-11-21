package tech.kayys.andalus.harness.planning;

/**
 * Signals that a plan revision was based on a stale or divergent history state.
 *
 * <p>The expected revision is the caller's observed current revision. The actual
 * revision is the latest revision recorded by the history store, or {@code -1}
 * when no record exists.</p>
 */
public final class PlanRevisionConflictException extends RuntimeException {

    private final PlanId planId;
    private final long expectedCurrentRevision;
    private final long actualCurrentRevision;

    public PlanRevisionConflictException(
            PlanId planId,
            long expectedCurrentRevision,
            long actualCurrentRevision,
            String detail
    ) {
        super("Plan revision conflict for " + planId
                + ": expected current revision " + expectedCurrentRevision
                + " but history is at " + actualCurrentRevision
                + (detail == null || detail.isBlank() ? "" : " (" + detail + ")"));
        this.planId = java.util.Objects.requireNonNull(planId, "planId is required");
        this.expectedCurrentRevision = expectedCurrentRevision;
        this.actualCurrentRevision = actualCurrentRevision;
    }

    public PlanId planId() {
        return planId;
    }

    public long expectedCurrentRevision() {
        return expectedCurrentRevision;
    }

    public long actualCurrentRevision() {
        return actualCurrentRevision;
    }
}
