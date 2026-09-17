package tech.kayys.wayang.harness.resource;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceScopeTest {

    @Test
    void testAllowAll() {
        DefaultResourceScope scope = DefaultResourceScope.allowAll();
        ResourceRequest req = ResourceRequest.of(ResourceType.CPU, "cpu-1");
        ResourceDecision decision = scope.evaluate(req);
        assertTrue(decision.isAllowed());
        assertFalse(decision.isDenied());
        assertFalse(decision.requiresApproval());
    }

    @Test
    void testAllowSpecificTypes() {
        DefaultResourceScope scope = DefaultResourceScope.of(ResourceType.WORKSPACE, ResourceType.CPU);
        assertTrue(scope.allows(ResourceRequest.of(ResourceType.WORKSPACE, "ws-1")));
        assertTrue(scope.allows(ResourceRequest.of(ResourceType.CPU, "cpu-0")));
        assertFalse(scope.allows(ResourceRequest.of(ResourceType.NETWORK, "net-0")));
    }

    @Test
    void testDenyByType() {
        DefaultResourceScope scope = DefaultResourceScope.builder()
                .deny(ResourceType.GPU)
                .build();
        ResourceDecision decision = scope.evaluate(ResourceRequest.of(ResourceType.GPU, "gpu-0"));
        assertTrue(decision.isDenied());
        assertFalse(decision.isAllowed());
    }

    @Test
    void testRequireApprovalForSecret() {
        DefaultResourceScope scope = DefaultResourceScope.builder()
                .requireApproval(ResourceType.SECRET)
                .build();
        ResourceDecision decision = scope.evaluate(ResourceRequest.of(ResourceType.SECRET, "token-key"));
        assertTrue(decision.requiresApproval());
        assertFalse(decision.isAllowed());
    }

    @Test
    void testDenySpecificResourceId() {
        DefaultResourceScope scope = DefaultResourceScope.builder()
                .denyResourceId("forbidden-db")
                .build();
        ResourceDecision denied = scope.evaluate(ResourceRequest.of(ResourceType.DATABASE, "forbidden-db"));
        assertTrue(denied.isDenied());

        ResourceDecision allowed = scope.evaluate(ResourceRequest.of(ResourceType.DATABASE, "allowed-db"));
        assertTrue(allowed.isAllowed());
    }

    @Test
    void testDenyTakesPrecedenceOverApproval() {
        // DENY wins when both type is denied and would require approval by predicate
        DefaultResourceScope scope = DefaultResourceScope.builder()
                .deny(ResourceType.CONTAINER)
                .requireApproval(ResourceType.CONTAINER)
                .build();
        ResourceDecision decision = scope.evaluate(ResourceRequest.of(ResourceType.CONTAINER, "ctr-1"));
        assertTrue(decision.isDenied(), "DENY must dominate over APPROVAL");
    }

    @Test
    void testResourceQuotaCanAllocate() {
        DefaultResourceQuota quota = DefaultResourceQuota.builder()
                .limit(ResourceType.CPU, 4, "cores")
                .build();

        ResourceRequest req = ResourceRequest.of(ResourceType.CPU, "cpu-1",
                ResourceConstraints.withMaximum(2L));
        assertTrue(quota.canAllocate(req));
        assertTrue(quota.tryAllocate(req));

        // After using 2, we have 2 left — another request of 2 should still pass
        assertTrue(quota.canAllocate(req));
        assertTrue(quota.tryAllocate(req));

        // Now at 4/4; next request of 2 should fail
        assertFalse(quota.canAllocate(req));
        assertFalse(quota.tryAllocate(req));
    }

    @Test
    void testResourceQuotaUsage() {
        DefaultResourceQuota quota = DefaultResourceQuota.builder()
                .limit(ResourceType.MEMORY, 1024, "MB")
                .build();
        quota.recordUsage(ResourceType.MEMORY, 256);
        assertEquals(256, quota.usage(ResourceType.MEMORY).current());
        assertEquals("MB", quota.usage(ResourceType.MEMORY).unit());

        quota.releaseUsage(ResourceType.MEMORY, 128);
        assertEquals(128, quota.usage(ResourceType.MEMORY).current());
    }

    @Test
    void testUnlimitedQuotaAlwaysAllows() {
        DefaultResourceQuota quota = DefaultResourceQuota.unlimited();
        ResourceRequest req = ResourceRequest.of(ResourceType.STORAGE, "large-volume",
                ResourceConstraints.withMaximum(Long.MAX_VALUE / 2));
        assertTrue(quota.canAllocate(req));
    }
}
