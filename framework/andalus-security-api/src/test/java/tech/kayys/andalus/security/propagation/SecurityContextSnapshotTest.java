package tech.kayys.andalus.security.propagation;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SecurityContextSnapshotTest {

    @Test
    void restoresPrincipalTenantAndAttributes() {
        SecurityContext original = new SecurityContext(
                Principal.agent("agent-a", "Agent A"),
                TenantContext.of("tenant-a"),
                Map.of("request-id", "req-123"));

        SecurityContext restored = SecurityContextSnapshot
                .from(original, Optional.empty())
                .restore();

        assertEquals(original.principal(), restored.principal());
        assertEquals("tenant-a", restored.tenant().tenantId());
        assertEquals("req-123", restored.attributes().get("request-id"));
    }

    @Test
    void restoredContextDoesNotShareMutableAttributeMap() {
        SecurityContext original = new SecurityContext(
                Principal.agent("agent-a", "Agent A"),
                TenantContext.of("tenant-a"),
                Map.of("scope", "documents.read"));

        SecurityContextSnapshot snapshot = SecurityContextSnapshot.from(original, Optional.empty());
        SecurityContext restored = snapshot.restore();

        assertThrows(UnsupportedOperationException.class,
                () -> restored.attributes().put("scope", "admin"));
        assertEquals("documents.read", snapshot.attributes().get("scope"));
    }

    @Test
    void snapshotRequiresSourceContext() {
        assertThrows(NullPointerException.class,
                () -> SecurityContextSnapshot.from(null, Optional.empty()));
    }

    @Test
    void missingDelegationIsNormalizedToEmpty() {
        SecurityContext context = SecurityContext.of(
                Principal.agent("agent-a", "Agent A"),
                TenantContext.of("tenant-a"));

        SecurityContextSnapshot snapshot =
                new SecurityContextSnapshot(context.principal(), context.tenant(), null, null);

        assertTrue(snapshot.delegation().isEmpty());
        assertTrue(snapshot.attributes().isEmpty());
    }
}
