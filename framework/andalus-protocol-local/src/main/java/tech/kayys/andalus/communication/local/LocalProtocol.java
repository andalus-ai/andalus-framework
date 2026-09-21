package tech.kayys.andalus.communication.local;

import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.capability.ProtocolCapability;
import tech.kayys.andalus.communication.endpoint.AgentEndpoint;
import tech.kayys.andalus.communication.endpoint.LocalAgentEndpoint;
import tech.kayys.andalus.communication.protocol.*;

import java.util.Objects;
import java.util.Set;

import static tech.kayys.andalus.communication.protocol.WellKnownProtocols.IN_PROCESS;

public final class LocalProtocol implements AgentProtocol {

    private final LocalAgentRegistry registry;
    private final LocalProtocolClient client;
    private final LocalProtocolServer server;

    public LocalProtocol(LocalAgentRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.client = new LocalProtocolClient(registry);
        this.server = new LocalProtocolServer(registry);
    }

    public LocalProtocol() {
        this(new DefaultLocalAgentRegistry());
    }

    public LocalAgentRegistry registry() {
        return registry;
    }

    @Override
    public ProtocolId id() {
        return IN_PROCESS;
    }

    @Override
    public ProtocolVersion version() {
        return new ProtocolVersion(1, 0);
    }

    @Override
    public Set<ProtocolCapability> capabilities() {
        return Set.of(
                ProtocolCapability.REQUEST_RESPONSE,
                ProtocolCapability.ASYNC_TASK,
                ProtocolCapability.STREAMING,
                ProtocolCapability.EVENTS
        );
    }

    @Override
    public boolean supports(
            AgentEndpoint endpoint,
            AgentRequest request
    ) {
        return endpoint instanceof LocalAgentEndpoint;
    }

    @Override
    public ProtocolClient client(ProtocolContext context) {
        return client;
    }

    @Override
    public ProtocolServer server() {
        return server;
    }
}
