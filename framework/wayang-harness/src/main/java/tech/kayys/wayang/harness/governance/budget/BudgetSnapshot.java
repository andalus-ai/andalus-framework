package tech.kayys.wayang.harness.governance.budget;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a budget snapshot.
 *
 * <p>Its components capture `snapshot id`, `timestamp`, `usages`, `limits`.</p>
 *
 * @param snapshotId the snapshot id
 * @param timestamp the timestamp
 * @param usages the usages
 * @param limits the limits
 */


public record BudgetSnapshot(
        String snapshotId,
        Instant timestamp,
        Map<BudgetDimension, BudgetUsage> usages,
        Map<BudgetDimension, BudgetLimit> limits
) {
    public BudgetSnapshot {
        snapshotId = snapshotId == null ? "snap-" + System.currentTimeMillis() : snapshotId;
        timestamp = timestamp == null ? Instant.now() : timestamp;
        usages = usages == null ? Map.of() : Map.copyOf(usages);
        limits = limits == null ? Map.of() : Map.copyOf(limits);
    }
}
