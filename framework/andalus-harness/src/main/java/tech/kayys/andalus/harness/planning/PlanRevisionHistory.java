package tech.kayys.andalus.harness.planning;

import java.util.List;

/**
 * Append-only storage contract for accepted plan revisions.
 *
 * <p>Implementations should preserve append order and reject duplicate transitions
 * where appropriate. Durable implementations belong to the runtime/infrastructure layer.</p>
 */
public interface PlanRevisionHistory {

    /** Append an accepted revision record. */
    void append(PlanRevisionRecord record);

    /** Return the revision records for a plan in ascending revision order. */
    List<PlanRevisionRecord> revisions(PlanId planId);
}
