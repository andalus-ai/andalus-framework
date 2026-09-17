package tech.kayys.wayang.harness.governance.budget;

import java.time.Instant;
import java.util.Objects;

public record BudgetConsumption(
        String executionId,
        String actionId,
        BudgetDimension dimension,
        BudgetAmount amount,
        Instant timestamp,
        String source
) {
    public BudgetConsumption {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(amount, "amount");
        actionId = actionId == null ? "unknown" : actionId;
        timestamp = timestamp == null ? Instant.now() : timestamp;
        source = source == null ? "runtime" : source;
    }
}
