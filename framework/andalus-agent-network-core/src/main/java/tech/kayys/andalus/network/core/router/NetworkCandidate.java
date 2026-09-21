package tech.kayys.andalus.network.core.router;

import tech.kayys.andalus.network.endpoint.ResolvedEndpoint;
import tech.kayys.andalus.network.protocol.AgentNetworkProtocol;

import java.util.Objects;

public record NetworkCandidate(
        ResolvedEndpoint endpoint,
        AgentNetworkProtocol protocol,
        int priority
) {
    public NetworkCandidate {
        Objects.requireNonNull(endpoint, "endpoint");
        Objects.requireNonNull(protocol, "protocol");
    }
}
