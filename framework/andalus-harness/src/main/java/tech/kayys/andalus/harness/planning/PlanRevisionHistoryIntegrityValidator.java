package tech.kayys.andalus.harness.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Validates a revision-history snapshot before it is trusted or restored.
 *
 * <p>Validation is intentionally read-only. Persistent stores can use it during
 * startup, migration, import, or integrity audits without coupling the
 * framework contract to a particular database.</p>
 */
public final class PlanRevisionHistoryIntegrityValidator {

    private PlanRevisionHistoryIntegrityValidator() {
    }

    /**
     * Checks plan identity, strict revision continuity, ordering, and snapshot
     * continuity. The supplied order is significant and is never silently
     * sorted or repaired.
     */
    public static PlanRevisionHistoryIntegrityReport validate(
            PlanId expectedPlanId,
            List<PlanRevisionRecord> records
    ) {
        Objects.requireNonNull(expectedPlanId, "expectedPlanId is required");
        Objects.requireNonNull(records, "records are required");

        List<String> errors = new ArrayList<>();
        PlanRevisionRecord previous = null;
        for (int index = 0; index < records.size(); index++) {
            PlanRevisionRecord current = records.get(index);
            if (current == null) {
                errors.add("Revision record at index " + index + " is null");
                previous = null;
                continue;
            }
            if (!expectedPlanId.equals(current.planId())) {
                errors.add("Revision record at index " + index + " belongs to another plan");
            }
            if (previous != null) {
                if (current.fromRevision() != previous.toRevision()) {
                    errors.add("Revision chain is discontinuous between indexes "
                            + (index - 1) + " and " + index);
                }
                if (current.toRevision() <= previous.toRevision()) {
                    errors.add("Revision records are not in strictly ascending order at index " + index);
                }
                if (!previous.revisedPlan().equals(current.previousPlan())) {
                    errors.add("Plan snapshot continuity is broken between indexes "
                            + (index - 1) + " and " + index);
                }
            }
            previous = current;
        }
        return new PlanRevisionHistoryIntegrityReport(errors);
    }
}
