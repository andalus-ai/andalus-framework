package tech.kayys.andalus.security.communication;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.communication.api.*;
import tech.kayys.andalus.communication.capability.CapabilityId;
import tech.kayys.andalus.communication.message.MessagePayload;
import tech.kayys.andalus.communication.protocol.ProtocolContext;
import tech.kayys.andalus.security.authz.AuthorizationDecision;
import tech.kayys.andalus.security.authz.AuthorizationService;
import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.delegation.*;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SecuredAgentCommunicatorDelegationTest {

    @Test
    void propagatesDelegationAndTargetResourceIntoAuthorizationRequest() {
        AgentRef target = AgentRef.local("agent-b", "Agent B", "runtime-1");
        AgentRequest request = AgentRequest.of(target, CapabilityId.of("documents.read"), MessagePayload.text("read"));
        AgentCommunicator delegate = new AgentCommunicator() {
            @Override
            public java.util.concurrent.CompletionStage<AgentResponse> send(AgentRequest r, ProtocolContext ctx) {
                return CompletableFuture.completedFuture(AgentResponse.success(MessagePayload.text("ok")));
            }
            @Override public AgentTask submit(AgentRequest r, ProtocolContext ctx) { return null; }
            @Override public java.util.concurrent.Flow.Publisher<AgentEvent> stream(AgentRequest r, ProtocolContext ctx) { return null; }
        };

        AtomicReference<tech.kayys.andalus.security.authz.AuthorizationRequest> captured = new AtomicReference<>();
        AuthorizationService authorization = authRequest -> {
            captured.set(authRequest);
            return CompletableFuture.completedFuture(AuthorizationDecision.allow());
        };

        SecurityContext securityContext = SecurityContext.of(
                Principal.agent("agent-a", "Agent A"), TenantContext.of("tenant-a"));
        DelegationContext delegation = new DelegationContext(
                DelegationId.generate(),
                DelegationAudience.of("agent-b"),
                new DelegationConstraints(List.of("documents.read"), List.of("execute"), 1),
                Instant.now(),
                Instant.now().plusSeconds(60),
                Optional.empty());
        ProtocolContext protocolContext = new ProtocolContext("tenant-a", Map.of(
                SecuredAgentCommunicator.SECURITY_CONTEXT_KEY, securityContext,
                SecuredAgentCommunicator.DELEGATION_CONTEXT_KEY, delegation));

        SecuredAgentCommunicator secured = new SecuredAgentCommunicator(delegate, authorization);
        secured.send(request, protocolContext).toCompletableFuture().join();

        assertNotNull(captured.get());
        assertEquals("agent-b", captured.get().resource());
        assertEquals(delegation, captured.get().delegation().orElseThrow());
    }
}
