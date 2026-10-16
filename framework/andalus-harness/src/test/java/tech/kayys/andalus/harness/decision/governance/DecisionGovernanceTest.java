package tech.kayys.andalus.harness.decision.governance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.context.ContextId;
import tech.kayys.andalus.harness.context.ContextSnapshot;
import tech.kayys.andalus.harness.context.DefaultAssembledContext;
import tech.kayys.andalus.harness.context.DefaultHarnessIdentity;
import tech.kayys.andalus.harness.decision.trace.DecisionTrace;
import tech.kayys.andalus.harness.decision.trace.DecisionTraceEntryType;
import tech.kayys.andalus.harness.decision.trace.DecisionTraceRequest;
import tech.kayys.andalus.harness.decision.trace.DecisionTraceRuntime;
import tech.kayys.andalus.harness.decision.trace.DefaultDecisionTraceRuntime;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.protocol.AgentAction;
import tech.kayys.andalus.harness.protocol.AgentDecision;
import tech.kayys.andalus.harness.protocol.AgentDecisionType;
import tech.kayys.andalus.harness.protocol.AgentRef;
import tech.kayys.andalus.harness.protocol.AgentTask;
import tech.kayys.andalus.harness.protocol.AgentTurnContext;
import tech.kayys.andalus.harness.protocol.BudgetView;
import tech.kayys.andalus.harness.protocol.CapabilityView;
import tech.kayys.andalus.harness.protocol.DecisionConstraints;
import tech.kayys.andalus.harness.protocol.DecisionContext;
import tech.kayys.andalus.harness.protocol.DefaultAgentDecision;
import tech.kayys.andalus.harness.protocol.HumanAction;
import tech.kayys.andalus.harness.protocol.HumanRequest;
import tech.kayys.andalus.harness.protocol.ModelCatalogView;
import tech.kayys.andalus.harness.protocol.ToolCatalogView;
import tech.kayys.andalus.tool.ToolArguments;
import tech.kayys.andalus.tool.resolution.ToolIntent;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DecisionGovernanceTest {

    private DecisionGovernance governance;
    private DecisionTraceRuntime traceRuntime;
    private AgentTurnContext turnContext;
    private DecisionContext decisionContext;

    @BeforeEach
    void setUp() {
        traceRuntime = new DefaultDecisionTraceRuntime();
        governance = new DefaultDecisionGovernance(null, traceRuntime);

        ExecutionId execId = ExecutionId.generate();
        DefaultAssembledContext assembled = new DefaultAssembledContext(
                ContextId.generate(), List.of(), Map.of(), 0L);
        ContextSnapshot snapshot = new ContextSnapshot("snap-1", Instant.now(), assembled, Map.of());

        turnContext = new AgentTurnContext() {
            @Override public ExecutionId executionId() { return execId; }
            @Override public tech.kayys.andalus.harness.context.HarnessIdentity identity() {
                return DefaultHarnessIdentity.of("test-agent");
            }
            @Override public ContextSnapshot context() { return snapshot; }
            @Override public CapabilityView capabilities() { return null; }
            @Override public BudgetView budget() { return null; }
            @Override public ToolCatalogView tools() { return null; }
            @Override public ModelCatalogView models() { return null; }
        };

        decisionContext = DecisionContext.of(turnContext);
    }

    @Test
    void testAllowedDecision() {
        ToolIntent intent = ToolIntent.of("calculator", ToolArguments.empty(), null);
        AgentDecision decision = DefaultAgentDecision.tool(intent);

        DecisionGovernanceRequest request = DecisionGovernanceRequest.of("exec-1", "turn-1", decision, decisionContext);
        DecisionGovernanceResult result = governance.evaluate(request).toCompletableFuture().join();

        assertEquals(GovernanceOutcome.ALLOW, result.outcome());
        assertTrue(result.reason().isPresent());
        assertTrue(result.findings().stream().anyMatch(f -> "GOVERNANCE_PASSED".equals(f.code())));
    }

    @Test
    void testStructuralValidationFailure_InvalidAction() {
        // EXECUTE_TOOL but action is HumanAction instead of ToolAction
        AgentDecision invalidDecision = new AgentDecision() {
            @Override public AgentDecisionType type() { return AgentDecisionType.EXECUTE_TOOL; }
            @Override public AgentAction action() { return new HumanAction(HumanRequest.of("prompt")); }
            @Override public Optional<String> reason() { return Optional.empty(); }
            @Override public Optional<Map<String, Object>> metadata() { return Optional.empty(); }
        };

        DecisionGovernanceRequest request = DecisionGovernanceRequest.of("exec-2", "turn-1", invalidDecision, decisionContext);
        DecisionGovernanceResult result = governance.evaluate(request).toCompletableFuture().join();

        assertEquals(GovernanceOutcome.DENY, result.outcome());
        assertTrue(result.findings().stream().anyMatch(f -> "STRUCTURAL_VALIDATION_FAILED".equals(f.code())));
    }

    @Test
    void testConstraintDisallowsToolUse() {
        DecisionConstraints constraints = new DecisionConstraints(
                Optional.empty(),
                Optional.empty(),
                Optional.of(3),
                true,
                false, // allowToolUse = false
                true,
                true,
                true,
                Map.of()
        );
        DecisionContext restrictedContext = new DecisionContext(
                turnContext,
                Optional.empty(),
                Optional.empty(),
                constraints,
                List.of(),
                Map.of()
        );

        ToolIntent intent = ToolIntent.of("calculator", ToolArguments.empty(), null);
        AgentDecision decision = DefaultAgentDecision.tool(intent);

        DecisionGovernanceRequest request = DecisionGovernanceRequest.of("exec-3", "turn-1", decision, restrictedContext);
        DecisionGovernanceResult result = governance.evaluate(request).toCompletableFuture().join();

        assertEquals(GovernanceOutcome.DENY, result.outcome());
        assertTrue(result.findings().stream().anyMatch(f -> "STRUCTURAL_VALIDATION_FAILED".equals(f.code())));
    }

    @Test
    void testHumanInTheLoop_AskHuman() {
        AgentDecision decision = DefaultAgentDecision.askHuman(HumanRequest.of("Need clarification"));

        DecisionGovernanceRequest request = DecisionGovernanceRequest.of("exec-4", "turn-1", decision, decisionContext);
        DecisionGovernanceResult result = governance.evaluate(request).toCompletableFuture().join();

        assertEquals(GovernanceOutcome.WAIT, result.outcome());
        assertEquals(1, result.obligations().size());
        assertEquals("HITL_APPROVAL", result.obligations().get(0).obligationType());
    }

    @Test
    void testApprovalRequired_Wait() {
        ToolIntent intent = ToolIntent.of("database.delete", ToolArguments.empty(), null);
        AgentDecision decision = DefaultAgentDecision.tool(intent);

        DecisionGovernanceRequest request = new DecisionGovernanceRequest(
                "exec-5",
                "turn-1",
                decision,
                decisionContext,
                Optional.empty(),
                Map.of("requiresApproval", true, "humanApproved", false)
        );

        DecisionGovernanceResult result = governance.evaluate(request).toCompletableFuture().join();

        assertEquals(GovernanceOutcome.WAIT, result.outcome());
        assertTrue(result.findings().stream().anyMatch(f -> "APPROVAL_REQUIRED".equals(f.code())));
        assertEquals(1, result.obligations().size());
    }

    @Test
    void testApprovalGranted_Allowed() {
        ToolIntent intent = ToolIntent.of("database.delete", ToolArguments.empty(), null);
        AgentDecision decision = DefaultAgentDecision.tool(intent);

        DecisionGovernanceRequest request = new DecisionGovernanceRequest(
                "exec-6",
                "turn-1",
                decision,
                decisionContext,
                Optional.empty(),
                Map.of("requiresApproval", true, "humanApproved", true)
        );

        DecisionGovernanceResult result = governance.evaluate(request).toCompletableFuture().join();

        assertEquals(GovernanceOutcome.ALLOW, result.outcome());
        assertTrue(result.findings().stream().anyMatch(f -> "HITL_APPROVED".equals(f.code())));
    }

    @Test
    void testRevalidationFailsAfterContextChange() {
        ToolIntent intent = ToolIntent.of("database.delete", ToolArguments.empty(), null);
        AgentDecision decision = DefaultAgentDecision.tool(intent);

        DecisionGovernanceRequest request = new DecisionGovernanceRequest(
                "exec-7",
                "turn-1",
                decision,
                decisionContext,
                Optional.empty(),
                Map.of("requiresApproval", true, "humanApproved", true, "approvalInvalidated", true)
        );

        DecisionGovernanceResult result = governance.evaluate(request).toCompletableFuture().join();

        assertEquals(GovernanceOutcome.DENY, result.outcome());
        assertTrue(result.findings().stream().anyMatch(f -> "APPROVAL_INVALIDATED".equals(f.code())));
    }

    @Test
    void testDelegationAuthorityExceeded() {
        AgentDecision decision = DefaultAgentDecision.delegate(
                AgentRef.of("sub-agent"),
                AgentTask.of("heavy-task")
        );

        DecisionGovernanceRequest request = new DecisionGovernanceRequest(
                "exec-8",
                "turn-1",
                decision,
                decisionContext,
                Optional.empty(),
                Map.of("delegationAuthorityExceeded", true)
        );

        DecisionGovernanceResult result = governance.evaluate(request).toCompletableFuture().join();

        assertEquals(GovernanceOutcome.DENY, result.outcome());
        assertTrue(result.findings().stream().anyMatch(f -> "DELEGATION_AUTHORITY_EXCEEDED".equals(f.code())));
    }

    @Test
    void testProhibitedProvider() {
        ToolIntent intent = ToolIntent.of("remote.service", ToolArguments.empty(), null);
        AgentDecision decision = DefaultAgentDecision.tool(intent);

        DecisionGovernanceRequest request = new DecisionGovernanceRequest(
                "exec-9",
                "turn-1",
                decision,
                decisionContext,
                Optional.empty(),
                Map.of("prohibitedProvider", true)
        );

        DecisionGovernanceResult result = governance.evaluate(request).toCompletableFuture().join();

        assertEquals(GovernanceOutcome.DENY, result.outcome());
        assertTrue(result.findings().stream().anyMatch(f -> "PROHIBITED_PROVIDER".equals(f.code())));
    }

    @Test
    void testBudgetExceeded() {
        ToolIntent intent = ToolIntent.of("calculator", ToolArguments.empty(), null);
        AgentDecision decision = DefaultAgentDecision.tool(intent);

        DecisionGovernanceRequest request = new DecisionGovernanceRequest(
                "exec-10",
                "turn-1",
                decision,
                decisionContext,
                Optional.empty(),
                Map.of("budgetExceeded", true)
        );

        DecisionGovernanceResult result = governance.evaluate(request).toCompletableFuture().join();

        assertEquals(GovernanceOutcome.DENY, result.outcome());
        assertTrue(result.findings().stream().anyMatch(f -> "BUDGET_EXCEEDED".equals(f.code())));
    }

    @Test
    void testDecisionTraceIntegration() {
        DecisionTrace trace = traceRuntime.start(DecisionTraceRequest.of("exec-11", "turn-1", "strat-gov"))
                .toCompletableFuture().join();

        ToolIntent intent = ToolIntent.of("calculator", ToolArguments.empty(), null);
        AgentDecision decision = DefaultAgentDecision.tool(intent);

        DecisionGovernanceRequest request = new DecisionGovernanceRequest(
                "exec-11",
                "turn-1",
                decision,
                decisionContext,
                Optional.of(trace.id()),
                Map.of()
        );

        DecisionGovernanceResult result = governance.evaluate(request).toCompletableFuture().join();
        assertEquals(GovernanceOutcome.ALLOW, result.outcome());

        DecisionTrace updatedTrace = traceRuntime.get(tech.kayys.andalus.harness.decision.trace.DecisionTraceQuery.byTraceId(trace.id()))
                .toCompletableFuture().join().orElseThrow();

        assertEquals(1, updatedTrace.entries().size());
        assertEquals(DecisionTraceEntryType.DECISION_VALIDATED, updatedTrace.entries().get(0).type());
        assertEquals("ALLOW", updatedTrace.entries().get(0).attributes().get("governanceOutcome"));
    }
}
