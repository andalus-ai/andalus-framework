package tech.kayys.andalus.network.core;

import tech.kayys.andalus.network.protocol.AgentNetworkProtocol;

import java.util.Collection;
import java.util.Optional;

public interface AgentNetworkProtocolRegistry {

    void register(AgentNetworkProtocol protocol);

    Optional<AgentNetworkProtocol> find(String protocolId);

    Collection<AgentNetworkProtocol> all();
}
