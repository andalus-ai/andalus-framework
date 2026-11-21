package tech.kayys.andalus.harness.planning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe, process-local revision history for tests and embedded deployments.
 *
 * <p>This implementation is not durable; production runtimes should provide a
 * persistent {@link PlanRevisionHistory}.</p>
 */
public final class InMemoryPlanRevisionHistory implements PlanRevisionHistory {

    private final Map<PlanId, List<PlanRevisionRecord>> records = new ConcurrentHashMap<>();

    @Override
    public void append(PlanRevisionRecord record) {
        Objects.requireNonNull(record, "record is required");
        compareAndAppend(record, record.fromRevision());
    }

    @Override
    public void compareAndAppend(PlanRevisionRecord record, long expectedCurrentRevision) {
        Objects.requireNonNull(record, "record is required");
        records.compute(record.planId(), (id, existing) -> {
            List<PlanRevisionRecord> updated = existing == null
                    ? new ArrayList<>() : new ArrayList<>(existing);
            long actualCurrentRevision = updated.isEmpty()
                    ? -1L : updated.get(updated.size() - 1).toRevision();

            if (expectedCurrentRevision != record.fromRevision()) {
                throw new PlanRevisionConflictException(
                        id, expectedCurrentRevision, actualCurrentRevision,
                        "expected revision must match the record's fromRevision");
            }
            if (!updated.isEmpty()) {
                PlanRevisionRecord latest = updated.get(updated.size() - 1);
                if (actualCurrentRevision != expectedCurrentRevision) {
                    throw new PlanRevisionConflictException(
                            id, expectedCurrentRevision, actualCurrentRevision,
                            "another revision was already appended");
                }
                if (!latest.revisedPlan().equals(record.previousPlan())) {
                    throw new PlanRevisionConflictException(
                            id, expectedCurrentRevision, actualCurrentRevision,
                            "previous plan snapshot differs from the latest accepted plan");
                }
            }
            updated.add(record);
            return List.copyOf(updated);
        });
    }

    @Override
    public List<PlanRevisionRecord> revisions(PlanId planId) {
        Objects.requireNonNull(planId, "planId is required");
        return records.getOrDefault(planId, List.of()).stream()
                .sorted(Comparator.comparingLong(PlanRevisionRecord::toRevision))
                .toList();
    }
}
