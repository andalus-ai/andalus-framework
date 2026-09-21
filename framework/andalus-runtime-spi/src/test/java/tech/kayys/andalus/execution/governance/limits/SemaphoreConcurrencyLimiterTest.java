package tech.kayys.andalus.execution.governance.limits;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.governance.PolicyEvaluationContext;
import tech.kayys.andalus.execution.governance.PolicyEvaluationContexts;
import tech.kayys.andalus.execution.governance.ToolCapabilityLevel;
import tech.kayys.andalus.execution.governance.ToolPermissionContext;
import tech.kayys.andalus.tool.SimpleToolInvocation;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SemaphoreConcurrencyLimiterTest {

    @Test
    void testConcurrencyLimitAndRelease() {
        SemaphoreConcurrencyLimiter limiter = new SemaphoreConcurrencyLimiter();
        LimitDefinition def = new LimitDefinition(
                "test-concurrency",
                LimitType.CONCURRENCY,
                1,
                null,
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

        // 1st acquire -> allow
        LimitCheckResult res1 = limiter.tryAcquire(def, ctx);
        assertTrue(res1.allowed());

        // 2nd acquire -> deny
        LimitCheckResult res2 = limiter.tryAcquire(def, ctx);
        assertTrue(res2.denied());

        // release
        limiter.release(def, ctx);

        // 3rd acquire -> allow
        LimitCheckResult res3 = limiter.tryAcquire(def, ctx);
        assertTrue(res3.allowed());
    }
}
