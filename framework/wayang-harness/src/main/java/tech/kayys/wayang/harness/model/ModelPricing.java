package tech.kayys.wayang.harness.model;

import java.math.BigDecimal;

/**
 * Represents a model pricing.
 *
 * <p>Its components capture `cost per million input tokens`, `cost per million output tokens`, `currency`.</p>
 *
 * @param costPerMillionInputTokens the cost per million input tokens
 * @param costPerMillionOutputTokens the cost per million output tokens
 * @param currency the currency
 */


public record ModelPricing(
        BigDecimal costPerMillionInputTokens,
        BigDecimal costPerMillionOutputTokens,
        String currency
) {
    public ModelPricing {
        if (costPerMillionInputTokens == null) {
            costPerMillionInputTokens = BigDecimal.ZERO;
        }
        if (costPerMillionOutputTokens == null) {
            costPerMillionOutputTokens = BigDecimal.ZERO;
        }
        if (currency == null) {
            currency = "USD";
        }
    }

    public static ModelPricing free() {
        return new ModelPricing(BigDecimal.ZERO, BigDecimal.ZERO, "USD");
    }

    public static ModelPricing of(double inputPerMillion, double outputPerMillion) {
        return new ModelPricing(BigDecimal.valueOf(inputPerMillion), BigDecimal.valueOf(outputPerMillion), "USD");
    }
}
