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
}
