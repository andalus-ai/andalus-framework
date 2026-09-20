package tech.kayys.wayang.execution.economics;

import java.time.Instant;
import java.util.Objects;

/**
 * Calculated monetary or dimensional cost of execution.
 */
public record Cost(
    double amount,
    String currency,
    Instant computedAt
) {
    public Cost {
        Objects.requireNonNull(currency, "currency cannot be null");
        Objects.requireNonNull(computedAt, "computedAt cannot be null");
    }

    public static Cost of(double amount, String currency) {
        return new Cost(amount, currency, Instant.now());
    }

    public static Cost zero(String currency) {
        return new Cost(0.0, currency, Instant.now());
    }
}
