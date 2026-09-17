package tech.kayys.wayang.execution.governance.audit;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.governance.*;
import tech.kayys.wayang.execution.governance.approval.*;
import tech.kayys.wayang.tool.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class ToolAuditEventTest {

    private InMemoryApprovalStore approvalStore = new InMemoryApprovalStore();
    private DefaultApprovalService approvalService = new DefaultApprovalService(approvalStore);
    private DefaultApprovalBindingValidator bindingValidator = new DefaultApprovalBindingValidator();
    private DefaultPolicyEvaluationContextFactory contextFactory = new DefaultPolicyEvaluationContextFactory();
    private InMemoryToolAuditPublisher auditPublisher = new InMemoryToolAuditPublisher();

    private SimpleToolInvocation invocation(String name) {
        return SimpleToolInvocation.of(name, Map.of("key", "val"));
    }

    private ToolContext toolContext() {
        return new SimpleToolContext(Map.of(
                "tenantId", "tenant-test",
                "userId", "user-test",
                "agentId", "agent-test",
                "executionId", "exec-test",
                "correlationId", "corr-test"
        ));
    }

    private ToolExecutor echoExecutor() {
        return (inv, ctx) -> CompletableFuture.completedFuture(
                SimpleToolResult.success(Map.of("res", "done"))
        );
    }

    @Test
    void allowedExecutionEmitsPolicyEvaluatedAndExecutionEvents() throws Exception {
        ToolPolicy allowPolicy = new DefaultToolPolicy(List.of());
        DefaultToolPolicyEvaluator evaluator = new DefaultToolPolicyEvaluator(List.of(allowPolicy));
        DefaultToolExecutionGuard guard = new DefaultToolExecutionGuard(
                contextFactory, evaluator, approvalService, bindingValidator, auditPublisher
        );

        guard.execute(invocation("fs.read"), toolContext(), echoExecutor()).get();

        List<ToolAuditEvent> events = auditPublisher.events();
        assertFalse(events.isEmpty());

        // Check types emitted
        List<ToolAuditEventType> types = events.stream().map(ToolAuditEvent::eventType).toList();
        assertTrue(types.contains(ToolAuditEventType.POLICY_EVALUATED));
        assertTrue(types.contains(ToolAuditEventType.EXECUTION_ALLOWED));
        assertTrue(types.contains(ToolAuditEventType.EXECUTION_STARTED));
        assertTrue(types.contains(ToolAuditEventType.EXECUTION_COMPLETED));

        // Invariant: Tenant isolation & execution correlation
        for (ToolAuditEvent ev : events) {
            assertEquals("tenant-test", ev.tenantId());
            assertEquals("exec-test", ev.executionId());
            assertEquals("corr-test", ev.correlationId());
            assertEquals("fs.read", ev.toolName());
        }
    }

    @Test
    void deniedExecutionEmitsDeniedAuditRecord() {
        ToolPolicy denyPolicy = new DefaultToolPolicy("strict", 10, PolicyDefaultEffect.DENY, List.of());
        DefaultToolPolicyEvaluator evaluator = new DefaultToolPolicyEvaluator(List.of(denyPolicy));
        DefaultToolExecutionGuard guard = new DefaultToolExecutionGuard(
                contextFactory, evaluator, approvalService, bindingValidator, auditPublisher
        );

        assertThrows(ExecutionException.class, () ->
                guard.execute(invocation("fs.write"), toolContext(), echoExecutor()).get()
        );

        List<ToolAuditEvent> events = auditPublisher.events();
        List<ToolAuditEventType> types = events.stream().map(ToolAuditEvent::eventType).toList();
        assertTrue(types.contains(ToolAuditEventType.POLICY_EVALUATED));
        assertTrue(types.contains(ToolAuditEventType.EXECUTION_DENIED));

        ToolAuditEvent deniedEvent = events.stream()
                .filter(e -> e.eventType() == ToolAuditEventType.EXECUTION_DENIED)
                .findFirst().orElseThrow();
        assertEquals("DENY", deniedEvent.decision());
        assertNotNull(deniedEvent.reason());
    }

    @Test
    void approvalWorkflowEmitsApprovalAuditEvents() throws Exception {
        ToolPolicyRule rule = ToolPolicyRule.builder("appr-rule")
                .tools(Set.of("db.delete"))
                .effect(PolicyEffect.REQUIRE_APPROVAL)
                .priority(1)
                .build();
        ToolPolicy approvalPolicy = new DefaultToolPolicy(List.of(rule));
        DefaultToolPolicyEvaluator evaluator = new DefaultToolPolicyEvaluator(List.of(approvalPolicy));
        DefaultToolExecutionGuard guard = new DefaultToolExecutionGuard(
                contextFactory, evaluator, approvalService, bindingValidator, auditPublisher
        );

        // Step 1: Initial call requests approval
        ExecutionException ex = assertThrows(ExecutionException.class, () ->
                guard.execute(invocation("db.delete"), toolContext(), echoExecutor()).get()
        );
        String approvalId = ((ToolApprovalRequiredException) ex.getCause()).approvalId();

        List<ToolAuditEventType> types1 = auditPublisher.events().stream().map(ToolAuditEvent::eventType).toList();
        assertTrue(types1.contains(ToolAuditEventType.APPROVAL_REQUESTED));

        // Step 2: Approver approves
        approvalService.approve(approvalId, "sec-admin", "Verified backup exists");

        // Step 3: Execute approved
        guard.executeApproved(approvalId, invocation("db.delete"), toolContext(), echoExecutor()).get();

        List<ToolAuditEventType> types2 = auditPublisher.events().stream().map(ToolAuditEvent::eventType).toList();
        assertTrue(types2.contains(ToolAuditEventType.APPROVAL_GRANTED));
        assertTrue(types2.contains(ToolAuditEventType.EXECUTION_COMPLETED));

        ToolAuditEvent grantedEvent = auditPublisher.events().stream()
                .filter(e -> e.eventType() == ToolAuditEventType.APPROVAL_GRANTED)
                .findFirst().orElseThrow();
        assertEquals("sec-admin", grantedEvent.approverId());
        assertEquals("Verified backup exists", grantedEvent.reason());
    }
}
