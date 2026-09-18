package tech.kayys.wayang.harness.protocol;

import java.util.Map;
import java.util.Optional;

/**
 * Defines the contract for agent decision operations in the Wayang framework.
 */


public interface AgentDecision {

    AgentDecisionType type();

    AgentAction action();

    Optional<String> reason();

    Optional<Map<String, Object>> metadata();
}
