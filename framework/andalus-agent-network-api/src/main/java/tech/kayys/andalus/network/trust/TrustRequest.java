package tech.kayys.andalus.network.trust;

import tech.kayys.andalus.communication.api.AgentRef;

import java.util.Objects;

public record TrustRequest(
        AgentRef agent,
        String protocol,
        String audience,
        String tenantId
) {

    public TrustRequest {
        Objects.requireNonNull(agent, "agent");
    }

    public static TrustRequest of(AgentRef agent, String protocol) {
        return new TrustRequest(agent, protocol, null, null);
    }
}
