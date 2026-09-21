package tech.kayys.andalus.network.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.communication.api.AgentRef;
import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.api.AgentResponse;
import tech.kayys.andalus.communication.capability.CapabilityId;
import tech.kayys.andalus.communication.message.MessagePayload;
import tech.kayys.andalus.network.AgentNetworkRequest;
import tech.kayys.andalus.network.AgentNetworkResponse;
import tech.kayys.andalus.network.AgentNetworkTask;
import tech.kayys.andalus.network.capability.CapabilityMatch;
import tech.kayys.andalus.network.capability.CapabilityNegotiator;
import tech.kayys.andalus.network.endpoint.AgentEndpointResolver;
import tech.kayys.andalus.network.endpoint.ResolvedEndpoint;
import tech.kayys.andalus.network.exception.AgentNetworkException;
import tech.kayys.andalus.network.exception.TrustException;
import tech.kayys.andalus.network.protocol.AgentNetworkClient;
import tech.kayys.andalus.network.protocol.AgentNetworkProtocol;
import tech.kayys.andalus.network.trust.AgentTrustService;
import tech.kayys.andalus.network.trust.TrustDecision;
import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.propagation.SecurityContextSnapshot;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class AgentNetworkRoutingTest {

    private DefaultAgentNetworkProtocolRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new DefaultAgentNetworkProtocolRegistry();
    }

    @Test
    void testRejectionWhenUntrusted() {
        AgentEndpointResolver resolver = agent -> CompletableFuture.completedFuture(
                ResolvedEndpoint.of(URI.create("https://remote/agent"), "ANP", "1.0")
        );

        AgentTrustService trustService = req -> CompletableFuture.completedFuture(
                TrustDecision.reject("Untrusted domain")
        );

        CapabilityNegotiator negotiator = (req, caps) -> CompletableFuture.completedFuture(
                CapabilityMatch.match(List.of("search"))
        );

        DefaultAgentNetwork network = new DefaultAgentNetwork(resolver, trustService, negotiator, registry);

        AgentRef target = AgentRef.remote("untrusted-agent", "Untrusted", URI.create("https://remote/agent"));
        AgentRequest agentReq = AgentRequest.of(target, CapabilityId.of("search"), MessagePayload.text("query"));
        SecurityContext secCtx = SecurityContext.of(Principal.anonymous(), TenantContext.empty());
        SecurityContextSnapshot secSnap = SecurityContextSnapshot.from(secCtx, Optional.empty());

        AgentNetworkRequest netReq = AgentNetworkRequest.of(agentReq, secSnap);

        Exception ex = assertThrows(Exception.class, () -> network.invoke(netReq).toCompletableFuture().join());
        assertTrue(ex.getCause() instanceof TrustException || ex instanceof TrustException);
    }

    @Test
    void testRejectionWhenCapabilityMismatch() {
        AgentEndpointResolver resolver = agent -> CompletableFuture.completedFuture(
                ResolvedEndpoint.of(URI.create("https://remote/agent"), "ANP", "1.0")
        );

        AgentTrustService trustService = req -> CompletableFuture.completedFuture(TrustDecision.accept());

        CapabilityNegotiator negotiator = (req, caps) -> CompletableFuture.completedFuture(
                CapabilityMatch.noMatch(List.of("admin.wipe"))
        );

        DefaultAgentNetwork network = new DefaultAgentNetwork(resolver, trustService, negotiator, registry);

        AgentRef target = AgentRef.remote("agent", "Agent", URI.create("https://remote/agent"));
        AgentRequest agentReq = AgentRequest.of(target, CapabilityId.of("admin.wipe"), MessagePayload.text("wipe"));
        SecurityContext secCtx = SecurityContext.of(Principal.anonymous(), TenantContext.empty());
        SecurityContextSnapshot secSnap = SecurityContextSnapshot.from(secCtx, Optional.empty());

        AgentNetworkRequest netReq = AgentNetworkRequest.of(agentReq, secSnap);

        Exception ex = assertThrows(Exception.class, () -> network.invoke(netReq).toCompletableFuture().join());
        assertTrue(ex.getCause() instanceof AgentNetworkException || ex instanceof AgentNetworkException);
        assertTrue(ex.getMessage().contains("Capability requirement not satisfied"));
    }
}
