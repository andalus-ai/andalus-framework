package tech.kayys.andalus.security.policy;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.security.authz.AuthorizationDecision;
import tech.kayys.andalus.security.authz.AuthorizationRequest;
import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.delegation.*;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.policy.engine.PolicyEngine;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class PolicyAuthorizationServiceDelegationTest {

    private static final Instant NOW = Instant.parse("2026-11-01T12:00:00Z");

    private SecurityContext context() {
        return contextForTarget("agent-b");
    }

    private SecurityContext contextForTarget(String targetAgentId) {
        return new SecurityContext(
                Principal.agent("agent-a", "Agent A"),
                TenantContext.of("tenant-a"),
                Map.of("delegated_to", targetAgentId));
    }

    private DelegationContext delegation(
            List<String> capabilities,
            List<String> actions,
            List<String> audience,
            Instant expiresAt
    ) {
        return new DelegationContext(
                DelegationId.generate(),
                new DelegationAudience(audience),
                new DelegationConstraints(capabilities, actions, 2),
                NOW.minusSeconds(5),
                expiresAt,
                Optional.empty()
        );
    }

    private PolicyAuthorizationService service(AtomicBoolean policyCalled) {
        PolicyEngine allowAll = request -> {
            policyCalled.set(true);
            return PolicyDecision.allow("test-allow", "policy allows");
        };
        return new PolicyAuthorizationService(allowAll, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void delegatedCapabilityIsCheckedBeforePolicyAllow() {
        AtomicBoolean policyCalled = new AtomicBoolean();
        var service = service(policyCalled);
        var request = new AuthorizationRequest(
                context(), "payments.transfer", "execute", "invoice:42", Map.of(),
                Optional.of(delegation(List.of("documents.read"), List.of("read"), List.of("agent-b"), NOW.plusSeconds(60))));

        AuthorizationDecision decision = service.authorize(request).toCompletableFuture().join();

        assertFalse(decision.allowed());
        assertTrue(decision.reason().contains("Capability"));
        assertFalse(policyCalled.get(), "policy allow must not widen delegated authority");
    }

    @Test
    void delegatedActionAndAudienceAreEnforced() {
        var service = service(new AtomicBoolean());
        var actionRequest = new AuthorizationRequest(
                context(), "documents.read", "delete", "invoice:42", Map.of(),
                Optional.of(delegation(List.of("documents.read"), List.of("read"), List.of("agent-b"), NOW.plusSeconds(60))));
        var audienceRequest = new AuthorizationRequest(
                contextForTarget("agent-c"), "documents.read", "read", "invoice:42", Map.of(),
                Optional.of(delegation(List.of("documents.read"), List.of("read"), List.of("agent-b"), NOW.plusSeconds(60))));

        assertFalse(service.authorize(actionRequest).toCompletableFuture().join().allowed());
        assertFalse(service.authorize(audienceRequest).toCompletableFuture().join().allowed());
    }

    @Test
    void expiredDelegationIsDenied() {
        var service = service(new AtomicBoolean());
        var request = new AuthorizationRequest(
                context(), "documents.read", "read", "invoice:42", Map.of(),
                Optional.of(delegation(List.of("*"), List.of("*"), List.of("agent-b"), NOW)));

        AuthorizationDecision decision = service.authorize(request).toCompletableFuture().join();

        assertFalse(decision.allowed());
        assertEquals("Delegation expired", decision.reason());
    }


    @Test
    void delegatedAudienceIsBoundToTargetAgentNotResourceIdentifier() {
        var service = service(new AtomicBoolean());
        var request = new AuthorizationRequest(
                context(), "documents.read", "read", "customer-record:123", Map.of(),
                Optional.of(delegation(List.of("documents.read"), List.of("read"), List.of("agent-b"), NOW.plusSeconds(60))));

        assertTrue(service.authorize(request).toCompletableFuture().join().allowed());
    }

    @Test
    void missingDelegatedTargetIsDenied() {
        var service = service(new AtomicBoolean());
        var contextWithoutTarget = SecurityContext.of(
                Principal.agent("agent-a", "Agent A"), TenantContext.of("tenant-a"));
        var request = new AuthorizationRequest(
                contextWithoutTarget, "documents.read", "read", "customer-record:123", Map.of(),
                Optional.of(delegation(List.of("documents.read"), List.of("read"), List.of("agent-b"), NOW.plusSeconds(60))));

        AuthorizationDecision decision = service.authorize(request).toCompletableFuture().join();

        assertFalse(decision.allowed());
        assertEquals("Delegated target agent is missing", decision.reason());
    }

    @Test
    void validDelegationStillRequiresPolicyAllow() {
        var service = new PolicyAuthorizationService(
                request -> PolicyDecision.deny("policy-deny", "tenant policy denies"),
                Clock.fixed(NOW, ZoneOffset.UTC));
        var request = new AuthorizationRequest(
                context(), "documents.read", "read", "agent-b", Map.of(),
                Optional.of(delegation(List.of("documents.read"), List.of("read"), List.of("agent-b"), NOW.plusSeconds(60))));

        AuthorizationDecision decision = service.authorize(request).toCompletableFuture().join();

        assertFalse(decision.allowed());
        assertEquals("tenant policy denies", decision.reason());
    }
}
