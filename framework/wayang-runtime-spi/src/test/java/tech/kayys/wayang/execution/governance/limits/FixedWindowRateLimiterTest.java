package tech.kayys.wayang.execution.governance.limits;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.governance.PolicyEvaluationContext;
import tech.kayys.wayang.execution.governance.PolicyEvaluationContexts;
import tech.kayys.wayang.execution.governance.ToolCapabilityLevel;
import tech.kayys.wayang.execution.governance.ToolPermissionContext;
import tech.kayys.wayang.tool.SimpleToolInvocation;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class FixedWindowRateLimiterTest {

    @Test
    void testRateLimitEnforcementAndWindowReset() {
        AtomicReference<Instant> currentInstant = new AtomicReference<>(Instant.parse("2026-09-18T10:00:00Z"));
        Clock mockClock = new Clock() {
            @Override
            public java.time.ZoneId getZone() {
                return ZoneOffset.UTC;
            }

            @Override
            public Clock withZone(java.time.ZoneId zone) {
                return this;
            }

            @Override
            public Instant instant() {
                return currentInstant.get();
            }
        };

        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(mockClock);
        LimitDefinition def = new LimitDefinition(
                "test-rate",
                LimitType.RATE,
                2,
                Duration.ofMinutes(1),
                "tenant",
                Map.of(),
                Map.of()
        );

        SimpleToolInvocation invocation = SimpleToolInvocation.of("tool-a", Map.of());
        ToolPermissionContext permCtx = new ToolPermissionContext("tenant-1", "user-1", "exec-1", List.of("user"), "tool-a", ToolCapabilityLevel.READ);
        PolicyEvaluationContext policyContext = PolicyEvaluationContexts.builder(permCtx, invocation)
                .tenantId("tenant-1")
                .userId("user-1")
                .agentId("agent-1")
                .executionId("exec-1")
                .build();

        LimitContext ctx = new LimitContext(policyContext, "tool-a", null, null, Map.of());

        // 1st request -> allow
        LimitCheckResult res1 = limiter.tryAcquire(def, ctx);
        assertTrue(res1.allowed());

        // 2nd request -> allow
        LimitCheckResult res2 = limiter.tryAcquire(def, ctx);
        assertTrue(res2.allowed());

        // 3rd request -> deny
        LimitCheckResult res3 = limiter.tryAcquire(def, ctx);
        assertTrue(res3.denied());
        assertEquals("test-rate", res3.limitId());
        assertNotNull(res3.retryAfter());

        // Advance clock past 1 minute
        currentInstant.set(currentInstant.get().plus(Duration.ofSeconds(61)));

        // 4th request -> allowed again
        LimitCheckResult res4 = limiter.tryAcquire(def, ctx);
        assertTrue(res4.allowed());
    }
}
