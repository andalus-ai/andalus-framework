package tech.kayys.wayang.harness.protocol;

import java.util.Objects;

/**
 * Represents a agent ref.
 *
 * <p>Its components capture `agent id`.</p>
 *
 * @param agentId the agent id
 */


public record AgentRef(String agentId) {
    public AgentRef {
        Objects.requireNonNull(agentId, "agentId cannot be null");
    }

    public static AgentRef of(String agentId) {
        return new AgentRef(agentId);
    }
}
