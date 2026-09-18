package tech.kayys.wayang.harness.protocol;

import java.util.Map;
import java.util.Optional;

public interface AgentDecision {

    AgentDecisionType type();

    AgentAction action();

    Optional<String> reason();

    Optional<Map<String, Object>> metadata();
}
