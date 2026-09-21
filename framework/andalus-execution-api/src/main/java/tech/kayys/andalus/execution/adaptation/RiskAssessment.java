package tech.kayys.andalus.execution.adaptation;

import java.util.Objects;

/**
 * Risk evaluation for an adaptation plan.
 */
public record RiskAssessment(
    RiskLevel risk,
    String rationale,
    boolean requiresHumanApproval
) {
    public RiskAssessment {
        Objects.requireNonNull(risk, "risk cannot be null");
        Objects.requireNonNull(rationale, "rationale cannot be null");
    }

    public static RiskAssessment low(String rationale) {
        return new RiskAssessment(RiskLevel.LOW, rationale, false);
    }

    public static RiskAssessment high(String rationale) {
        return new RiskAssessment(RiskLevel.HIGH, rationale, true);
    }

    public static RiskAssessment unacceptable(String rationale) {
        return new RiskAssessment(RiskLevel.UNACCEPTABLE, rationale, false);
    }

    public boolean isFeasible() {
        return risk != RiskLevel.UNACCEPTABLE;
    }
}
