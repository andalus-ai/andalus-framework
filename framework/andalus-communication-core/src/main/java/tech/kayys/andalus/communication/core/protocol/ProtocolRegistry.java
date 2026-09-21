package tech.kayys.andalus.communication.core.protocol;

import tech.kayys.andalus.communication.protocol.AgentProtocol;
import tech.kayys.andalus.communication.protocol.ProtocolId;

import java.util.Collection;
import java.util.Optional;

public interface ProtocolRegistry {

    void register(AgentProtocol protocol);

    void unregister(ProtocolId protocolId);

    Optional<AgentProtocol> find(ProtocolId protocolId);

    Collection<AgentProtocol> protocols();

    boolean contains(ProtocolId protocolId);
}
