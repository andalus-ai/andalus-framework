package tech.kayys.wayang.execution.governance.limits;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.governance.PolicyEvaluationContext;
import tech.kayys.wayang.execution.governance.PolicyEvaluationContexts;
import tech.kayys.wayang.execution.governance.ToolCapabilityLevel;
import tech.kayys.wayang.execution.governance.ToolPermissionContext;
import tech.kayys.wayang.tool.SimpleToolInvocation;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultLimitManagerTest {

    @Test
    void testLimitManagerAcquiresAllAndCommits() {
        LimitDefinition rateDef = new LimitDefinition("rate-1", LimitType.RATE, 10, Duration.ofMinutes(1), "tenant", Map.of(), Map.of());
        LimitDefinition concDef = new LimitDefinition("conc-1", LimitType.CONCURRENCY, 5, null, "tenant", Map.of(), Map.of());

        RateLimiter rateLimiter = new FixedWindowRateLimiter(Clock.systemUTC());
        ConcurrencyLimiter concurrencyLimiter = new SemaphoreConcurrencyLimiter();

        LimitController rateCtrl = new RateLimitController(rateDef, rateLimiter);
        LimitController concCtrl = new ConcurrencyLimitController(concDef, concurrencyLimiter);

        DefaultLimitManager manager = new DefaultLimitManager(List.of(rateCtrl, concCtrl));

        SimpleToolInvocation invocation = SimpleToolInvocation.of("tool-a", Map.of());
        ToolPermissionContext permCtx = new ToolPermissionContext("tenant-1", "user-1", "exec-1", List.of("user"), "tool-a", ToolCapabilityLevel.READ);
        PolicyEvaluationContext policyContext = PolicyEvaluationContexts.builder(permCtx, invocation)
                .tenantId("tenant-1")
                .userId("user-1")
                .agentId("agent-1")
                .executionId("exec-1")
                .build();

        LimitContext ctx = new LimitContext(policyContext, "tool-a", null, null, Map.of());

        LimitReservation reservation = manager.acquire(ctx);
        assertTrue(reservation.active());

        reservation.commit();
        assertFalse(reservation.active());
    }

    @Test
    void testLimitManagerRollsBackOnPartialFailure() {
        LimitDefinition concDef1 = new LimitDefinition("conc-1", LimitType.CONCURRENCY, 1, null, "tenant", Map.of(), Map.of());
        LimitDefinition concDef2 = new LimitDefinition("conc-2", LimitType.CONCURRENCY, 0, null, "tenant", Map.of(), Map.of());

        ConcurrencyLimiter concurrencyLimiter = new SemaphoreConcurrencyLimiter();

        LimitController ctrl1 = new ConcurrencyLimitController(concDef1, concurrencyLimiter);
        LimitController ctrl2 = new ConcurrencyLimitController(concDef2, concurrencyLimiter);

        DefaultLimitManager manager = new DefaultLimitManager(List.of(ctrl1, ctrl2));

        SimpleToolInvocation invocation = SimpleToolInvocation.of("tool-a", Map.of());
        ToolPermissionContext permCtx = new ToolPermissionContext("tenant-1", "user-1", "exec-1", List.of("user"), "tool-a", ToolCapabilityLevel.READ);
        PolicyEvaluationContext policyContext = PolicyEvaluationContexts.builder(permCtx, invocation)
                .tenantId("tenant-1")
                .userId("user-1")
                .agentId("agent-1")
                .executionId("exec-1")
                .build();

        LimitContext ctx = new LimitContext(policyContext, "tool-a", null, null, Map.of());

        assertThrows(LimitExceededException.class, () -> manager.acquire(ctx));

        // Verify that ctrl1's permit was rolled back (can acquire again)
        LimitReservation res = ctrl1.acquire(ctx);
        assertNotNull(res);
        res.rollback();
    }
}
