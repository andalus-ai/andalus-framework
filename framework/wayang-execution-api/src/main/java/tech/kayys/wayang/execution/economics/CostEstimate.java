package tech.kayys.wayang.execution.economics;

import java.util.Objects;

/**
 * Expected cost range for execution requirements.
 */
public record CostEstimate(
    double minimumExpected,
    double maximumExpected,
    String currency
) {
    public CostEstimate {
        Objects.requireNonNull(currency, "currency cannot be null");
    }

    public static CostEstimate of(double min, double max, String currency) {
        return new CostEstimate(min, max, currency);
    }
}
