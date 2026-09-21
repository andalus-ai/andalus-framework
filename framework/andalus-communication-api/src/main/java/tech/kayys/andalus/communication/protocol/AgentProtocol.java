package tech.kayys.andalus.communication.protocol;

import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.capability.ProtocolCapability;
import tech.kayys.andalus.communication.endpoint.AgentEndpoint;

import java.util.Set;

public interface AgentProtocol {

    ProtocolId id();

    ProtocolVersion version();

    Set<ProtocolCapability> capabilities();

    boolean supports(
            AgentEndpoint endpoint,
            AgentRequest request
    );

    ProtocolClient client(
            ProtocolContext context
    );

    default ProtocolServer server() {
        return null;
    }
}
