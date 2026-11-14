package tech.kayys.andalus.security.propagation;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.delegation.DelegationConstraints;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSecurityContextPropagatorTest {

    private final DefaultSecurityContextPropagator propagator =
            new DefaultSecurityContextPropagator(60);

    @Test
    void implicitDelegationRequestIsDenied() {
        PropagationRequest request = PropagationRequest.of(
                SecurityContext.of(
                        tech.kayys.andalus.security.identity.Principal.agent("agent-a", "Agent A"),
                        TenantContext.of("tenant-a")),
                "agent-b");

        assertThrows(SecurityException.class, () -> propagator.propagate(request));
    }

    @Test
    void explicitGrantIsPropagatedWithReducedHopCountAndTenantBinding() {
        SecurityContext source = SecurityContext.of(
                tech.kayys.andalus.security.identity.Principal.agent("agent-a", "Agent A"),
                TenantContext.of("tenant-a"));
        PropagationRequest request = PropagationRequest.of(
                source,
                "agent-b",
                new DelegationConstraints(List.of("documents.read"), List.of("read"), 2));

        PropagationResult result = propagator.propagate(request);

        assertEquals("tenant-a", result.context().tenant().tenantId());
        assertEquals("tenant-a", result.delegation().orElseThrow().tenantId().orElseThrow());
        assertEquals("agent-a", result.context().attributes().get("delegated_by"));
        assertEquals("agent-b", result.context().attributes().get("delegated_to"));
        assertEquals(1, result.delegation().orElseThrow().constraints().maxHops());
        assertTrue(result.delegation().orElseThrow().audience().allows("agent-b"));
    }

    @Test
    void tenantlessSourceCannotCreateDelegation() {
        SecurityContext source = SecurityContext.of(
                tech.kayys.andalus.security.identity.Principal.agent("agent-a", "Agent A"),
                TenantContext.empty());
        PropagationRequest request = PropagationRequest.of(
                source,
                "agent-b",
                new DelegationConstraints(List.of("documents.read"), List.of("read"), 2));

        assertThrows(SecurityException.class, () -> propagator.propagate(request));
    }

    @Test
    void invalidTtlIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new DefaultSecurityContextPropagator(0));
    }
}
