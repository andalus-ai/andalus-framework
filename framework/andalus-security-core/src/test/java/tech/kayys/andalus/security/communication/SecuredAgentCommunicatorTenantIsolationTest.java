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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SecuredAgentCommunicatorTenantIsolationTest {

    private static AgentRequest request() {
        return AgentRequest.of(
                AgentRef.local("target-agent", "Target Agent", "runtime"),
                CapabilityId.of("documents.read"),
                MessagePayload.text("read")
        );
    }

    private static AgentCommunicator delegate(AtomicInteger calls) {
        return new AgentCommunicator() {
            @Override
            public CompletableFuture<AgentResponse> send(AgentRequest request, ProtocolContext context) {
                calls.incrementAndGet();
                return CompletableFuture.completedFuture(
                        AgentResponse.success(MessagePayload.text("ok")));
            }

            @Override
            public AgentTask submit(AgentRequest request, ProtocolContext context) {
                calls.incrementAndGet();
                return null;
            }

            @Override
            public java.util.concurrent.Flow.Publisher<AgentEvent> stream(
                    AgentRequest request, ProtocolContext context) {
                calls.incrementAndGet();
                return null;
            }
        };
    }

    private static SecurityContext userContext(String tenantId) {
        return SecurityContext.of(Principal.user("user-1", "User"), TenantContext.of(tenantId));
    }

    @Test
    void rejectsAuthenticatedPrincipalWithoutTenantBeforeAuthorization() {
        AtomicInteger delegateCalls = new AtomicInteger();
        AtomicInteger authorizationCalls = new AtomicInteger();
        AuthorizationService authorization = request -> {
            authorizationCalls.incrementAndGet();
            return CompletableFuture.completedFuture(AuthorizationDecision.allow("allowed"));
        };
        SecuredAgentCommunicator secured = new SecuredAgentCommunicator(
                delegate(delegateCalls), authorization);
        SecurityContext missingTenant = SecurityContext.of(
                Principal.user("user-1", "User"), TenantContext.empty());

        assertThrows(AuthorizationException.class,
                () -> secured.send(request(), new ProtocolContext(null,
                        Map.of(SecuredAgentCommunicator.SECURITY_CONTEXT_KEY, missingTenant))));

        assertEquals(0, authorizationCalls.get());
        assertEquals(0, delegateCalls.get());
    }

    @Test
    void rejectsProtocolTenantThatConflictsWithAuthenticatedTenant() {
        AtomicInteger delegateCalls = new AtomicInteger();
        AtomicInteger authorizationCalls = new AtomicInteger();
        AuthorizationService authorization = request -> {
            authorizationCalls.incrementAndGet();
            return CompletableFuture.completedFuture(AuthorizationDecision.allow("allowed"));
        };
        SecuredAgentCommunicator secured = new SecuredAgentCommunicator(
                delegate(delegateCalls), authorization);
        ProtocolContext context = new ProtocolContext("tenant-B",
                Map.of(SecuredAgentCommunicator.SECURITY_CONTEXT_KEY, userContext("tenant-A")));

        assertThrows(AuthorizationException.class, () -> secured.send(request(), context));
        assertEquals(0, authorizationCalls.get());
        assertEquals(0, delegateCalls.get());
    }

    @Test
    void protocolTenantCannotEstablishTenantForAnonymousOrMissingContext() {
        AtomicInteger delegateCalls = new AtomicInteger();
        AtomicInteger authorizationCalls = new AtomicInteger();
        AuthorizationService authorization = request -> {
            authorizationCalls.incrementAndGet();
            return CompletableFuture.completedFuture(AuthorizationDecision.allow("allowed"));
        };
        SecuredAgentCommunicator secured = new SecuredAgentCommunicator(
                delegate(delegateCalls), authorization);

        assertThrows(AuthorizationException.class,
                () -> secured.send(request(), ProtocolContext.of("tenant-A")));
        assertEquals(0, authorizationCalls.get());
        assertEquals(0, delegateCalls.get());
    }

    @Test
    void matchingAuthenticatedTenantIsPassedToAuthorization() {
        AtomicInteger delegateCalls = new AtomicInteger();
        AtomicReference<String> authorizedTenant = new AtomicReference<>();
        AuthorizationService authorization = request -> {
            authorizedTenant.set(request.securityContext().tenant().tenantId());
            return CompletableFuture.completedFuture(AuthorizationDecision.allow("allowed"));
        };
        SecuredAgentCommunicator secured = new SecuredAgentCommunicator(
                delegate(delegateCalls), authorization);
        ProtocolContext context = new ProtocolContext("tenant-A",
                Map.of(SecuredAgentCommunicator.SECURITY_CONTEXT_KEY, userContext("tenant-A")));

        assertDoesNotThrow(() -> secured.send(request(), context).toCompletableFuture().join());
        assertEquals("tenant-A", authorizedTenant.get());
        assertEquals(1, delegateCalls.get());
    }
}
