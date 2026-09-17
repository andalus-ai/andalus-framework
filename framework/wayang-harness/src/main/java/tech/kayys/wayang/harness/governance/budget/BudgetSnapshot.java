package tech.kayys.wayang.harness.governance.budget;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

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
