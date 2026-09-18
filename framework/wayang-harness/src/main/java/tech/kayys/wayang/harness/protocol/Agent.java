package tech.kayys.wayang.harness.protocol;

/**
 * Defines the contract for agent operations in the Wayang framework.
 */


@FunctionalInterface
public interface Agent {

    AgentDecision decide(AgentTurnContext context);
}
