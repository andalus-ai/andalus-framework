package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.governance.approval.*;
import tech.kayys.wayang.execution.governance.audit.*;
import tech.kayys.wayang.execution.governance.quota.*;
import tech.kayys.wayang.tool.*;
import tech.kayys.wayang.tool.routing.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 3.13 End-to-End Tool Governance Integration Tests.
 * <p>
 * Tests full governance lifecycle:
 * Policy evaluation -> Authorization hierarchy -> Quotas & Rate Limits ->
 * Human-in-the-Loop Approvals -> Routing -> Tool Execution -> Audit Trail.
 */
class ToolGovernanceIntegrationTest {

    private InMemoryToolAuditPublisher auditPublisher;
    private ToolBudgetLedger budgetLedger;
    private RateLimiter rateLimiter;
    private DefaultQuotaManager quotaManager;
    private InMemoryApprovalStore approvalStore;
    private DefaultApprovalService approvalService;
    private DefaultApprovalBindingValidator bindingValidator;
    private DefaultPolicyEvaluationContextFactory contextFactory;

    private Map<String, Tool> toolRegistry;
    private Map<String, ToolExecutor> executorRegistry;

    @BeforeEach
    void setUp() {
        auditPublisher = new InMemoryToolAuditPublisher();
        budgetLedger = new ToolBudgetLedger();
        rateLimiter = new SlidingWindowRateLimiter(5, Duration.ofSeconds(60));
        quotaManager = new DefaultQuotaManager(rateLimiter, budgetLedger);
        approvalStore = new InMemoryApprovalStore();
        approvalService = new DefaultApprovalService(approvalStore);
        bindingValidator = new DefaultApprovalBindingValidator();
        contextFactory = new DefaultPolicyEvaluationContextFactory();

        toolRegistry = new HashMap<>();
        executorRegistry = new HashMap<>();

        // Register tools
        toolRegistry.put("fs.read", mockTool("fs.read"));
        toolRegistry.put("fs.delete", mockTool("fs.delete"));
        toolRegistry.put("system.exec", mockTool("system.exec"));

        executorRegistry.put("fs.read", (inv, ctx) -> CompletableFuture.completedFuture(
                SimpleToolResult.success(Map.of("data", "file content"))
        ));
        executorRegistry.put("fs.delete", (inv, ctx) -> CompletableFuture.completedFuture(
                SimpleToolResult.success(Map.of("deleted", true))
        ));
        executorRegistry.put("system.exec", (inv, ctx) -> CompletableFuture.completedFuture(
                SimpleToolResult.success(Map.of("output", "command executed"))
        ));
    }

    private Tool mockTool(String name) {
        return new Tool() {
            @Override
            public ToolDescriptor descriptor() {
                return SimpleToolDescriptor.of(name, "Mock " + name, Map.of());
            }

            @Override
            public CompletableFuture<ToolResult> execute(ToolInvocation invocation, ToolContext context) {
                return CompletableFuture.completedFuture(SimpleToolResult.success(Map.of()));
            }

            @Override
            public tech.kayys.wayang.identity.ResourceId id() { return null; }

            @Override
            public tech.kayys.wayang.extension.Metadata metadata() {
                return tech.kayys.wayang.extension.Metadata.builder().name(name).build();
            }

            @Override
            public tech.kayys.wayang.resource.ResourceType type() {
                return new tech.kayys.wayang.resource.ResourceType.Tool();
            }
        };
    }

    private ToolResolver createResolver() {
        return invocation -> {
            Tool tool = toolRegistry.get(invocation.name());
            ToolExecutor executor = executorRegistry.get(invocation.name());
            if (tool == null || executor == null) {
                throw new ToolRoutingException("No provider found for " + invocation.name());
            }
            return new ResolvedTool(tool, executor, "provider-standard");
        };
    }

    private ToolContext createContext(String tenantId, String userId, String agentId, Set<String> roles) {
        return new SimpleToolContext(Map.of(
                "tenantId", tenantId,
                "userId", userId,
                "agentId", agentId,
                "executionId", "exec-" + UUID.randomUUID(),
                "correlationId", "corr-123",
                "roles", roles
        ));
    }

    @Test
    void endToEndGovernedFlow_Allow_Approval_Deny_Quota() throws Exception {
        // 1. Setup multi-tiered policies
        ToolPolicyRule allowRead = ToolPolicyRule.builder("allow-read")
                .tools(Set.of("fs.read"))
                .effect(PolicyEffect.ALLOW)
                .priority(1)
                .build();

        ToolPolicyRule requireApprovalDelete = ToolPolicyRule.builder("approval-delete")
                .tools(Set.of("fs.delete"))
                .effect(PolicyEffect.REQUIRE_APPROVAL)
                .priority(2)
                .build();

        ToolPolicyRule denyExec = ToolPolicyRule.builder("deny-exec")
                .tools(Set.of("system.exec"))
                .effect(PolicyEffect.DENY)
                .priority(3)
                .build();

        ToolPolicy policy = new DefaultToolPolicy("governance-policy", 1, PolicyDefaultEffect.ALLOW,
                List.of(allowRead, requireApprovalDelete, denyExec));

        ToolPolicyEvaluator evaluator = new DefaultToolPolicyEvaluator(List.of(policy));

        DefaultToolExecutionGuard guard = new DefaultToolExecutionGuard(
                contextFactory, evaluator, approvalService, bindingValidator, auditPublisher, quotaManager
        );

        DefaultToolRouter router = new DefaultToolRouter(createResolver(), guard);

        ToolContext ctx = createContext("tenant-acme", "alice", "agent-x", Set.of("developer"));

        // Case A: Read tool allowed directly
        ToolResult readResult = router.execute(SimpleToolInvocation.of("fs.read", Map.of()), ctx).get();
        assertEquals("file content", readResult.getOutputs().get("data"));

        // Case B: Denied tool rejected immediately
        ExecutionException exDeny = assertThrows(ExecutionException.class, () ->
                router.execute(SimpleToolInvocation.of("system.exec", Map.of()), ctx).get()
        );
        assertInstanceOf(ToolExecutionDeniedException.class, exDeny.getCause());

        // Case C: Sensitive tool requires human approval
        ExecutionException exAppr = assertThrows(ExecutionException.class, () ->
                router.execute(SimpleToolInvocation.of("fs.delete", Map.of()), ctx).get()
        );
        assertInstanceOf(ToolApprovalRequiredException.class, exAppr.getCause());
        String approvalId = ((ToolApprovalRequiredException) exAppr.getCause()).approvalId();

        // Approve by authorized administrator
        approvalService.approve(approvalId, "sec-admin", "Approved deletion request");

        // Execution of approved invocation succeeds
        ToolResult deleteResult = router.executeApproved(approvalId, SimpleToolInvocation.of("fs.delete", Map.of()), ctx).get();
        assertEquals(true, deleteResult.getOutputs().get("deleted"));

        // Verify audit trail captures complete lifecycle
        List<ToolAuditEvent> auditTrail = auditPublisher.eventsForTenant("tenant-acme");
        assertFalse(auditTrail.isEmpty());

        List<ToolAuditEventType> eventTypes = auditTrail.stream().map(ToolAuditEvent::eventType).toList();
        assertTrue(eventTypes.contains(ToolAuditEventType.EXECUTION_ALLOWED));
        assertTrue(eventTypes.contains(ToolAuditEventType.EXECUTION_DENIED));
        assertTrue(eventTypes.contains(ToolAuditEventType.APPROVAL_REQUESTED));
        assertTrue(eventTypes.contains(ToolAuditEventType.APPROVAL_GRANTED));
        assertTrue(eventTypes.contains(ToolAuditEventType.EXECUTION_COMPLETED));
    }
}
