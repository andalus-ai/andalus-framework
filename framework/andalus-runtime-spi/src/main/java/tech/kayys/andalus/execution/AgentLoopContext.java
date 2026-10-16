package tech.kayys.andalus.execution;

import tech.kayys.andalus.agent.AgentContext;
import tech.kayys.andalus.core.AgentDefinition;

import java.util.Map;
import java.util.Objects;

/**
 * Immutable context shared by the agent loop during one execution.
 */
public record AgentLoopContext(
        AgentDefinition definition,
        AgentContext agentContext,
        Map<String, Object> attributes
) {

    public AgentLoopContext {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(agentContext, "agentContext");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
