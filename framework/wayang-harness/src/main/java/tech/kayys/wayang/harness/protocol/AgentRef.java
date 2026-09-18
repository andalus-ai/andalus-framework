package tech.kayys.wayang.harness.protocol;

import java.util.Objects;

public record AgentRef(String agentId) {
    public AgentRef {
        Objects.requireNonNull(agentId, "agentId cannot be null");
    }

    public static AgentRef of(String agentId) {
        return new AgentRef(agentId);
    }
}
