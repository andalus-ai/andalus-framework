package tech.kayys.wayang.execution.governance.quota;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.governance.*;
import tech.kayys.wayang.execution.governance.approval.*;
import tech.kayys.wayang.execution.governance.audit.*;
import tech.kayys.wayang.tool.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class QuotaManagerTest {

    private ToolBudgetLedger budgetLedger;
    private RateLimiter rateLimiter;
    private DefaultQuotaManager quotaManager;
    private InMemoryToolAuditPublisher auditPublisher;
    private DefaultPolicyEvaluationContextFactory contextFactory;
    private InMemoryApprovalStore approvalStore;
    private DefaultApprovalService approvalService;
    private DefaultApprovalBindingValidator bindingValidator;

    @BeforeEach
    void setUp() {
        budgetLedger = new ToolBudgetLedger();
        rateLimiter = new SlidingWindowRateLimiter(2, Duration.ofSeconds(10));
        quotaManager = new DefaultQuotaManager(rateLimiter, budgetLedger);
        auditPublisher = new InMemoryToolAuditPublisher();
        contextFactory = new DefaultPolicyEvaluationContextFactory();
        approvalStore = new InMemoryApprovalStore();
        approvalService = new DefaultApprovalService(approvalStore);
        bindingValidator = new DefaultApprovalBindingValidator();
    }

    private SimpleToolInvocation invocation(String name) {
        return SimpleToolInvocation.of(name, Map.of());
    }

    private ToolContext toolContext(String tenantId) {
        return new SimpleToolContext(Map.of(
                "tenantId", tenantId,
                "userId", "user-1",
                "executionId", "exec-1"
        ));
    }

    private ToolExecutor echoExecutor() {
        return (inv, ctx) -> CompletableFuture.completedFuture(
                SimpleToolResult.success(Map.of("status", "ok"))
        );
    }

    @Test
    void rateLimitEnforcedAcrossCalls() throws Exception {
        ToolPolicy allowPolicy = new DefaultToolPolicy(List.of());
        DefaultToolPolicyEvaluator evaluator = new DefaultToolPolicyEvaluator(List.of(allowPolicy));
        DefaultToolExecutionGuard guard = new DefaultToolExecutionGuard(
                contextFactory, evaluator, approvalService, bindingValidator, auditPublisher, quotaManager
        );

        // Call 1: Allowed
        guard.execute(invocation("fs.read"), toolContext("tenant-a"), echoExecutor()).get();

        // Call 2: Allowed
        guard.execute(invocation("fs.read"), toolContext("tenant-a"), echoExecutor()).get();

        // Call 3: Rate limit exceeded (limit is 2 permits per 10s)
        ExecutionException ex = assertThrows(ExecutionException.class, () ->
                guard.execute(invocation("fs.read"), toolContext("tenant-a"), echoExecutor()).get()
        );
        assertInstanceOf(QuotaExceededException.class, ex.getCause());
        QuotaExceededException qe = (QuotaExceededException) ex.getCause();
        assertEquals("RATE_LIMIT_EXCEEDED", qe.reason());

        // Verify audit event emitted for rate limit
        List<ToolAuditEvent> events = auditPublisher.events();
        assertTrue(events.stream().anyMatch(e -> e.eventType() == ToolAuditEventType.RATE_LIMIT_EXCEEDED));
    }

    @Test
    void budgetExhaustedRejectsExecution() {
        // Register budget with limit of 1 call
        ToolBudget budget = new ToolBudget("tenant-limited", null, 1, -1.0, -1);
        budgetLedger.register(budget);

        ToolPolicy allowPolicy = new DefaultToolPolicy(List.of());
        DefaultToolPolicyEvaluator evaluator = new DefaultToolPolicyEvaluator(List.of(allowPolicy));
        DefaultToolExecutionGuard guard = new DefaultToolExecutionGuard(
                contextFactory, evaluator, approvalService, bindingValidator, auditPublisher, quotaManager
        );

        // Call 1 succeeds
        assertDoesNotThrow(() ->
                guard.execute(invocation("fs.read"), toolContext("tenant-limited"), echoExecutor()).get()
        );

        // Call 2 fails due to exhausted budget
        ExecutionException ex = assertThrows(ExecutionException.class, () ->
                guard.execute(invocation("fs.read"), toolContext("tenant-limited"), echoExecutor()).get()
        );
        assertInstanceOf(QuotaExceededException.class, ex.getCause());
        QuotaExceededException qe = (QuotaExceededException) ex.getCause();
        assertEquals("CALL_BUDGET_EXHAUSTED", qe.reason());

        assertTrue(auditPublisher.events().stream().anyMatch(e -> e.eventType() == ToolAuditEventType.BUDGET_EXCEEDED));
    }
}
