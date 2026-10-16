package tech.kayys.andalus.observability;

import java.math.BigDecimal;
import java.util.Map;

public record CostEstimate(
        BigDecimal amount,
        String currency,
        String basis,
        Map<String, Object> breakdown
) {

    public CostEstimate {
        amount = amount == null
                ? BigDecimal.ZERO
                : amount;

        currency = currency == null || currency.isBlank()
                ? "USD"
                : currency;

        basis = basis == null
                ? "unknown"
                : basis;

        breakdown = breakdown == null
                ? Map.of()
                : Map.copyOf(breakdown);
    }
}
