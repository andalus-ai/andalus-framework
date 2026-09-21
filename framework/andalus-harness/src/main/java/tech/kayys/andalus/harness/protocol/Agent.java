package tech.kayys.andalus.harness.protocol;

/**
 * Defines the contract for agent operations in the Andalus framework.
 */


@FunctionalInterface
public interface Agent {

    AgentDecision decide(AgentTurnContext context);
}
