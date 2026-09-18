package tech.kayys.wayang.harness.model;

import java.math.BigDecimal;

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
