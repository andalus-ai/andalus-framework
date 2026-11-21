package tech.kayys.andalus.execution.governance;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TenantToolPolicyTest {

    private static ToolPermissionContext context(String tenantId) {
        return new ToolPermissionContext(
                tenantId, "user-1", "execution-1", List.of(), "documents.read", ToolCapabilityLevel.READ);
    }

    @Test
    void trimsConfiguredTenantIdsAndPolicyId() {
        TenantToolPolicy policy = new TenantToolPolicy(" tenant-policy ", Set.of(" tenant-a "), 7);

        PolicyDecision decision = policy.evaluate(null, context("tenant-a"));

        assertTrue(decision.isAllowed());
        assertEquals("tenant-policy", policy.id());
        assertEquals(7, policy.priority());
        assertEquals(AuthorizationScope.TENANT, policy.scope());
    }

    @Test
    void deniesMissingAndUnlistedTenants() {
        TenantToolPolicy policy = new TenantToolPolicy("tenant-policy", Set.of("tenant-a"), 50);

        PolicyDecision missing = policy.evaluate(null, context(null));
        PolicyDecision other = policy.evaluate(null, context("tenant-b"));

        assertTrue(missing.isDenied());
        assertTrue(other.isDenied());
        assertEquals(PolicyDecisionReason.TENANT_NOT_AUTHORIZED, missing.decisionReason());
        assertEquals(PolicyDecisionReason.TENANT_NOT_AUTHORIZED, other.decisionReason());
    }

    @Test
    void emptyAllowListIsDenyAll() {
        TenantToolPolicy policy = new TenantToolPolicy("tenant-policy", (Set<String>) null, 50);

        assertTrue(policy.evaluate(null, context("tenant-a")).isDenied());
        assertTrue(policy.evaluate(null, context(null)).isDenied());
    }

    @Test
    void rejectsBlankOrNullConfiguredTenantIdentifiers() {
        assertThrows(IllegalArgumentException.class,
                () -> new TenantToolPolicy("tenant-policy", Set.of("tenant-a", " "), 50));

        Set<String> withNull = new HashSet<>();
        withNull.add("tenant-a");
        withNull.add(null);
        assertThrows(IllegalArgumentException.class,
                () -> new TenantToolPolicy("tenant-policy", withNull, 50));
    }

    @Test
    void snapshotsAllowListAtConstruction() {
        Set<String> configured = new HashSet<>(Set.of("tenant-a"));
        TenantToolPolicy policy = new TenantToolPolicy("tenant-policy", configured, 50);
        configured.add("tenant-b");

        assertTrue(policy.evaluate(null, context("tenant-a")).isAllowed());
        assertTrue(policy.evaluate(null, context("tenant-b")).isDenied());
    }

    @Test
    void rejectsBlankPolicyId() {
        assertThrows(IllegalArgumentException.class,
                () -> new TenantToolPolicy("  ", Set.of("tenant-a"), 50));
    }
}
