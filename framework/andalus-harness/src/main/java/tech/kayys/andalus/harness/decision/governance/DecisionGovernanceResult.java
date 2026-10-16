package tech.kayys.andalus.harness.decision.governance;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Outcome and findings from evaluating a decision against governance constraints and policies.
 */
public record DecisionGovernanceResult(
        GovernanceOutcome outcome,
        Optional<String> reason,
        List<GovernanceFinding> findings,
        List<GovernanceObligation> obligations,
        Optional<String> policyDecisionReference,
        Optional<String> securityDecisionReference,
        Optional<String> traceReference,
        Map<String, Object> metadata
) {
    public DecisionGovernanceResult {
        if (outcome == null) {
            throw new IllegalArgumentException("outcome is required");
        }

        reason = reason == null ? Optional.empty() : reason;
        findings = findings == null ? List.of() : List.copyOf(findings);
        obligations = obligations == null ? List.of() : List.copyOf(obligations);
        policyDecisionReference = policyDecisionReference == null ? Optional.empty() : policyDecisionReference;
        securityDecisionReference = securityDecisionReference == null ? Optional.empty() : securityDecisionReference;
        traceReference = traceReference == null ? Optional.empty() : traceReference;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static DecisionGovernanceResult allowed(String reason) {
        return new DecisionGovernanceResult(
                GovernanceOutcome.ALLOW,
                Optional.ofNullable(reason),
                List.of(),
                List.of(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of()
        );
    }

    public static DecisionGovernanceResult allowed(String reason, List<GovernanceFinding> findings) {
        return new DecisionGovernanceResult(
                GovernanceOutcome.ALLOW,
                Optional.ofNullable(reason),
                findings,
                List.of(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of()
        );
    }

    public static DecisionGovernanceResult denied(String reason, List<GovernanceFinding> findings) {
        return new DecisionGovernanceResult(
                GovernanceOutcome.DENY,
                Optional.ofNullable(reason),
                findings,
                List.of(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of()
        );
    }

    public static DecisionGovernanceResult waitRequired(
            String reason,
            List<GovernanceFinding> findings,
            List<GovernanceObligation> obligations
    ) {
        return new DecisionGovernanceResult(
                GovernanceOutcome.WAIT,
                Optional.ofNullable(reason),
                findings,
                obligations,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of()
        );
    }

    public static DecisionGovernanceResult error(String reason, Throwable cause) {
        return new DecisionGovernanceResult(
                GovernanceOutcome.ERROR,
                Optional.ofNullable(reason),
                List.of(GovernanceFinding.error("GOVERNANCE_ERROR", "governance", cause != null ? cause.getMessage() : reason)),
                List.of(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of()
        );
    }
}
