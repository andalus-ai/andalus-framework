package tech.kayys.andalus.harness.planning;

import java.util.Objects;

/**
 * Filters revision history for one plan.
 *
 * <p>Revision bounds are inclusive and apply to each record's {@code toRevision}.
 * A null optional filter means that dimension is unrestricted.</p>
 */
public record PlanRevisionQuery(
        PlanId planId,
        Long fromRevisionInclusive,
        Long toRevisionInclusive,
        PlanRevisionPolicy revisionPolicy,
        Boolean completedWorkReplaced
) {
    public PlanRevisionQuery {
        Objects.requireNonNull(planId, "planId is required");
        if (fromRevisionInclusive != null && fromRevisionInclusive < 0) {
            throw new IllegalArgumentException("fromRevisionInclusive must be non-negative");
        }
        if (toRevisionInclusive != null && toRevisionInclusive < 0) {
            throw new IllegalArgumentException("toRevisionInclusive must be non-negative");
        }
        if (fromRevisionInclusive != null && toRevisionInclusive != null
                && fromRevisionInclusive > toRevisionInclusive) {
            throw new IllegalArgumentException("revision range start must not exceed its end");
        }
    }

    public static PlanRevisionQuery forPlan(PlanId planId) {
        return new PlanRevisionQuery(planId, null, null, null, null);
    }
}
