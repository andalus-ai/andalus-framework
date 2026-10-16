package tech.kayys.andalus.harness.protocol;

import java.util.concurrent.CompletionStage;

/**
 * Coordinates strategy invocation and decision validation for an agent turn.
 * Produces an intended {@link AgentDecision} without executing it.
 */
public interface DecisionEngine {

    CompletionStage<AgentDecision> decide(DecisionContext context);
}
