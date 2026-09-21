package tech.kayys.andalus.communication.endpoint;

public sealed interface AgentEndpoint
        permits LocalAgentEndpoint, RemoteAgentEndpoint {

    EndpointType type();
}
