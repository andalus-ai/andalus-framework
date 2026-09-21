package tech.kayys.andalus.communication.core.protocol;

import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.protocol.AgentProtocol;
import tech.kayys.andalus.communication.protocol.ProtocolContext;

import java.util.Collection;
import java.util.List;

public interface ProtocolSelectionPolicy {

    List<ProtocolCandidate> rank(
            Collection<AgentProtocol> protocols,
            AgentRequest request,
            ProtocolContext context
    );
}
