package tech.kayys.andalus.harness.protocol;

import java.util.concurrent.CompletionStage;

/**
 * Strategy contract for determining an agent's intended next decision given a {@link DecisionContext}.
 */
public interface DecisionStrategy {

    /**
     * Unique identifier for this strategy.
     */
    String id();

    /**
     * Produces an asynchronous decision based on the turn's decision context.
     *
     * @param context the turn-scoped decision context
     * @return completion stage of the selected decision
     */
    CompletionStage<AgentDecision> decide(DecisionContext context);
}
