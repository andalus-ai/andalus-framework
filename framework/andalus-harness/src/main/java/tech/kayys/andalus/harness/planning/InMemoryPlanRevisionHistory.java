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
        records.compute(record.planId(), (id, existing) -> {
            List<PlanRevisionRecord> updated = existing == null
                    ? new ArrayList<>() : new ArrayList<>(existing);
            if (!updated.isEmpty()) {
                PlanRevisionRecord latest = updated.get(updated.size() - 1);
                if (record.fromRevision() != latest.toRevision()) {
                    throw new IllegalStateException("Revision history is not contiguous for plan " + id);
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
