package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.governance.approval.*;
import tech.kayys.wayang.tool.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class ToolExecutionGuardTest {

    private InMemoryApprovalStore approvalStore;
    private DefaultApprovalService approvalService;
    private DefaultApprovalBindingValidator bindingValidator;
    private DefaultPolicyEvaluationContextFactory contextFactory;

    @BeforeEach
    void setUp() {
        approvalStore = new InMemoryApprovalStore();
        approvalService = new DefaultApprovalService(approvalStore);
        bindingValidator = new DefaultApprovalBindingValidator();
        contextFactory = new DefaultPolicyEvaluationContextFactory();
    }

    private SimpleToolInvocation invocation(String name) {
        return SimpleToolInvocation.of(name, Map.of());
    }

    private ToolContext toolContext() {
        return new SimpleToolContext(Map.of(
                "tenantId", "tenant-1",
                "userId", "user-1",
                "agentId", "agent-1",
                "executionId", "exec-1"
        ));
    }

    private ToolExecutor echoExecutor() {
        return (inv, ctx) -> CompletableFuture.completedFuture(
                SimpleToolResult.success(Map.of("output", "result-" + inv.name()))
        );
    }

    @Test
    void allowedPolicyExecutesDelegate() throws Exception {
        ToolPolicy allowPolicy = new DefaultToolPolicy(List.of());
        DefaultToolPolicyEvaluator evaluator = new DefaultToolPolicyEvaluator(List.of(allowPolicy));
        DefaultToolExecutionGuard guard = new DefaultToolExecutionGuard(contextFactory, evaluator, approvalService, bindingValidator);

        ToolResult result = guard.execute(invocation("fs.read"), toolContext(), echoExecutor()).get();
        assertEquals("result-fs.read", result.getOutputs().get("output"));
    }

    @Test
    void deniedPolicyThrowsDeniedException() {
        ToolPolicy denyPolicy = new DefaultToolPolicy("strict", 10, PolicyDefaultEffect.DENY, List.of());
        DefaultToolPolicyEvaluator evaluator = new DefaultToolPolicyEvaluator(List.of(denyPolicy));
        DefaultToolExecutionGuard guard = new DefaultToolExecutionGuard(contextFactory, evaluator, approvalService, bindingValidator);

        CompletableFuture<ToolResult> future = guard.execute(invocation("fs.write"), toolContext(), echoExecutor());
        ExecutionException ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ToolExecutionDeniedException.class, ex.getCause());
    }

    @Test
    void approvalRequiredPolicyCreatesApprovalAndThrows() {
        ToolPolicyRule rule = ToolPolicyRule.builder("appr-rule")
                .tools(java.util.Set.of("fs.write"))
                .effect(PolicyEffect.REQUIRE_APPROVAL)
                .priority(10)
                .build();
        ToolPolicy approvalPolicy = new DefaultToolPolicy(List.of(rule));
        DefaultToolPolicyEvaluator evaluator = new DefaultToolPolicyEvaluator(List.of(approvalPolicy));
        DefaultToolExecutionGuard guard = new DefaultToolExecutionGuard(contextFactory, evaluator, approvalService, bindingValidator);

        CompletableFuture<ToolResult> future = guard.execute(invocation("fs.write"), toolContext(), echoExecutor());
        ExecutionException ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ToolApprovalRequiredException.class, ex.getCause());

        String approvalId = ((ToolApprovalRequiredException) ex.getCause()).approvalId();
        assertNotNull(approvalId);

        ApprovalRequest req = approvalService.get(approvalId);
        assertEquals(ApprovalStatus.PENDING, req.status());
    }

    @Test
    void executeApprovedRunsAfterHumanApproval() throws Exception {
        ToolPolicyRule rule = ToolPolicyRule.builder("appr-rule")
                .tools(java.util.Set.of("fs.write"))
                .effect(PolicyEffect.REQUIRE_APPROVAL)
                .priority(10)
                .build();
        ToolPolicy approvalPolicy = new DefaultToolPolicy(List.of(rule));
        DefaultToolPolicyEvaluator evaluator = new DefaultToolPolicyEvaluator(List.of(approvalPolicy));
        DefaultToolExecutionGuard guard = new DefaultToolExecutionGuard(contextFactory, evaluator, approvalService, bindingValidator);

        // Step 1: Initial call requests approval
        CompletableFuture<ToolResult> future1 = guard.execute(invocation("fs.write"), toolContext(), echoExecutor());
        ExecutionException ex = assertThrows(ExecutionException.class, future1::get);
        String approvalId = ((ToolApprovalRequiredException) ex.getCause()).approvalId();

        // Step 2: Human approves
        approvalService.approve(approvalId, "admin", "Approved for task");

        // Step 3: executeApproved succeeds
        ToolResult result = guard.executeApproved(approvalId, invocation("fs.write"), toolContext(), echoExecutor()).get();
        assertEquals("result-fs.write", result.getOutputs().get("output"));
    }

    @Test
    void governedToolExecutorDelegatesToGuard() throws Exception {
        ToolPolicy allowPolicy = new DefaultToolPolicy(List.of());
        DefaultToolPolicyEvaluator evaluator = new DefaultToolPolicyEvaluator(List.of(allowPolicy));
        DefaultToolExecutionGuard guard = new DefaultToolExecutionGuard(contextFactory, evaluator, approvalService, bindingValidator);

        GovernedToolExecutor governed = new GovernedToolExecutor(echoExecutor(), guard);
        ToolResult result = governed.execute(invocation("fs.read"), toolContext()).get();
        assertEquals("result-fs.read", result.getOutputs().get("output"));
    }
}
