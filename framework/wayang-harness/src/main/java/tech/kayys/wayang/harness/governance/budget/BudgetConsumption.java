package tech.kayys.wayang.harness.governance.budget;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a budget consumption.
 *
 * <p>Its components capture `execution id`, `action id`, `dimension`, `amount`, `timestamp`, and other values.</p>
 *
 * @param executionId the execution id
 * @param actionId the action id
 * @param dimension the dimension
 * @param amount the amount
 * @param timestamp the timestamp
 * @param source the source
 */


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
