package tech.kayys.andalus.harness.protocol;

/**
 * Validates structural and constraint invariants for an {@link AgentDecision}
 * prior to execution.
 */
public interface DecisionValidator {

    DecisionValidationResult validate(
            AgentDecision decision,
            DecisionContext context
    );
}
