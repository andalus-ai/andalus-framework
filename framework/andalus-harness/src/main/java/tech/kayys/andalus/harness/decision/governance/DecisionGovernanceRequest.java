package tech.kayys.andalus.harness.decision.governance;

import tech.kayys.andalus.harness.protocol.AgentDecision;
import tech.kayys.andalus.harness.protocol.DecisionContext;

import java.util.Map;
import java.util.Optional;

/**
 * Request to evaluate an agent's proposed decision against governance constraints and policies.
 */
public record DecisionGovernanceRequest(
        String executionId,
        String turnId,
        AgentDecision decision,
        DecisionContext context,
        Optional<String> decisionTraceId,
        Map<String, Object> attributes
) {
    public DecisionGovernanceRequest {
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId is required");
        }
        if (turnId == null || turnId.isBlank()) {
            throw new IllegalArgumentException("turnId is required");
        }
        if (decision == null) {
            throw new IllegalArgumentException("decision is required");
        }
        if (context == null) {
            throw new IllegalArgumentException("context is required");
        }

        decisionTraceId = decisionTraceId == null ? Optional.empty() : decisionTraceId;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static DecisionGovernanceRequest of(
            String executionId,
            String turnId,
            AgentDecision decision,
            DecisionContext context
    ) {
        return new DecisionGovernanceRequest(
                executionId,
                turnId,
                decision,
                context,
                Optional.empty(),
                Map.of()
        );
    }
}
