package tech.kayys.wayang.harness.protocol;

@FunctionalInterface
public interface Agent {

    AgentDecision decide(AgentTurnContext context);
}
