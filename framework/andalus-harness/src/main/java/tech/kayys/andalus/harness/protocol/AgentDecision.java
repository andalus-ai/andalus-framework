package tech.kayys.andalus.harness.protocol;

import java.util.Map;
import java.util.Optional;

/**
 * Defines the contract for agent decision operations in the Andalus framework.
 */


public interface AgentDecision {

    AgentDecisionType type();

    AgentAction action();

    Optional<String> reason();

    Optional<Map<String, Object>> metadata();

    static AgentDecision fail(String reason) {
        return DefaultAgentDecision.fail(reason);
    }

    static AgentDecision complete(String summary, Object result) {
        return DefaultAgentDecision.complete(summary, result);
    }
}
