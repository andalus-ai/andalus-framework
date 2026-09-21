package tech.kayys.andalus.security.communication;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.communication.api.*;
import tech.kayys.andalus.communication.capability.CapabilityId;
import tech.kayys.andalus.communication.message.MessagePayload;
import tech.kayys.andalus.communication.protocol.ProtocolContext;
import tech.kayys.andalus.security.authz.AuthorizationDecision;
import tech.kayys.andalus.security.authz.AuthorizationService;
import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.exception.AuthorizationException;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class SecuredAgentCommunicatorTest {

    @Test
    void testAuthorizedCallProceedsToDelegate() {
        AgentRef target = AgentRef.local("target-1", "Target Agent", "runtime-1");
        AgentRequest request = AgentRequest.of(target, CapabilityId.of("calculate"), MessagePayload.text("1+1"));

        // Mock delegate communicator
        AgentCommunicator delegate = new AgentCommunicator() {
            @Override
            public java.util.concurrent.CompletionStage<AgentResponse> send(AgentRequest r, ProtocolContext ctx) {
                return CompletableFuture.completedFuture(AgentResponse.success(MessagePayload.text("2")));
            }
            @Override
            public AgentTask submit(AgentRequest r, ProtocolContext ctx) { return null; }
            @Override
            public java.util.concurrent.Flow.Publisher<AgentEvent> stream(AgentRequest r, ProtocolContext ctx) { return null; }
        };

        // Permissive authorization service
        AuthorizationService authService = authReq -> CompletableFuture.completedFuture(AuthorizationDecision.allow("granted"));

        SecuredAgentCommunicator secured = new SecuredAgentCommunicator(delegate, authService);

        Principal principal = new Principal("client-1", "Client", tech.kayys.andalus.security.identity.IdentityType.SERVICE, Map.of());
        SecurityContext secCtx = SecurityContext.of(principal, TenantContext.of("tenant-1"));
        ProtocolContext protoCtx = new ProtocolContext("tenant-1", Map.of(SecuredAgentCommunicator.SECURITY_CONTEXT_KEY, secCtx));

        AgentResponse response = secured.send(request, protoCtx).toCompletableFuture().join();
        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("2", response.payload().value());
    }

    @Test
    void testDeniedCallThrowsAuthorizationException() {
        AgentRef target = AgentRef.local("target-1", "Target Agent", "runtime-1");
        AgentRequest request = AgentRequest.of(target, CapabilityId.of("secret.read"), MessagePayload.text("key"));

        AgentCommunicator delegate = new AgentCommunicator() {
            @Override
            public java.util.concurrent.CompletionStage<AgentResponse> send(AgentRequest r, ProtocolContext ctx) {
                return CompletableFuture.completedFuture(AgentResponse.success(MessagePayload.text("secret")));
            }
            @Override
            public AgentTask submit(AgentRequest r, ProtocolContext ctx) { return null; }
            @Override
            public java.util.concurrent.Flow.Publisher<AgentEvent> stream(AgentRequest r, ProtocolContext ctx) { return null; }
        };

        // Denying authorization service
        AuthorizationService authService = authReq -> CompletableFuture.completedFuture(AuthorizationDecision.deny("Forbidden"));

        SecuredAgentCommunicator secured = new SecuredAgentCommunicator(delegate, authService);

        CompletableFuture<AgentResponse> future = secured.send(request, ProtocolContext.empty()).toCompletableFuture();
        Exception ex = assertThrows(Exception.class, future::join);
        assertTrue(ex.getCause() instanceof AuthorizationException || ex instanceof AuthorizationException);
    }
}
