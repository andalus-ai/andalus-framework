package tech.kayys.andalus.harness.planning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.context.ContextId;
import tech.kayys.andalus.harness.context.ContextSnapshot;
import tech.kayys.andalus.harness.context.DefaultAssembledContext;
import tech.kayys.andalus.harness.context.DefaultHarnessIdentity;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.protocol.AgentTurnContext;
import tech.kayys.andalus.harness.protocol.BudgetView;
import tech.kayys.andalus.harness.protocol.CapabilityView;
import tech.kayys.andalus.harness.protocol.DecisionContext;
import tech.kayys.andalus.harness.protocol.ModelCatalogView;
import tech.kayys.andalus.harness.protocol.ToolCatalogView;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.assertj.core.api.Assertions.assertThat;

class PlanningRuntimeTest {

    private AgentTurnContext turnContext;
    private DecisionContext decisionContext;

    @BeforeEach
    void setUp() {
        ExecutionId executionId = ExecutionId.generate();
        DefaultAssembledContext assembled = new DefaultAssembledContext(
                ContextId.generate(), List.of(), Map.of(), 0L);
        ContextSnapshot snapshot = new ContextSnapshot("snap-1", Instant.now(), assembled, Map.of());

        turnContext = new AgentTurnContext() {
            @Override public ExecutionId executionId() { return executionId; }
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

    // ── helper factories ──────────────────────────────────────────────────────

    private AgentPlan singleStepPlan() {
        return new AgentPlan(
                PlanId.generate(),
                "exec-1",
                Optional.empty(),
                0L,
                PlanStatus.PROPOSED,
                List.of(PlanStep.of("Research the topic")),
                Instant.now(),
                Map.of()
        );
    }

    private AgentPlan twoStepPlan() {
        PlanStep step1 = PlanStep.of("Step 1: gather data");
        PlanStep step2 = PlanStep.of("Step 2: analyse data", List.of(step1.id()));
        return new AgentPlan(
                PlanId.generate(),
                "exec-2",
                Optional.empty(),
                0L,
                PlanStatus.PROPOSED,
                List.of(step1, step2),
                Instant.now(),
                Map.of()
        );
    }

    // ── planner helpers ───────────────────────────────────────────────────────

    private Planner plannerFor(AgentPlan result) {
        return new Planner() {
            @Override public String id() { return "test-planner"; }
            @Override public CompletionStage<PlanningResult> plan(PlanningRequest request) {
                return CompletableFuture.completedFuture(PlanningResult.success(result));
            }
        };
    }

    private Planner failingPlanner() {
        return new Planner() {
            @Override public String id() { return "failing-planner"; }
            @Override public CompletionStage<PlanningResult> plan(PlanningRequest request) {
                CompletableFuture<PlanningResult> future = new CompletableFuture<>();
                future.completeExceptionally(new RuntimeException("planner exploded"));
                return future;
            }
        };
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    @Test
    void simplePlanCreation_returnsSuccess() {
        PlannerRegistry registry = new PlannerRegistry();
        registry.register(plannerFor(singleStepPlan()));

        PlanningRuntime runtime = new DefaultPlanningRuntime(registry);
        PlanningRequest request = PlanningRequest.of(decisionContext);

        PlanningResult result = runtime.createPlan(request).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.plan()).isPresent();
        assertThat(result.plan().get().steps()).hasSize(1);
    }

    @Test
    void multiStepPlanWithDependencies_validatesSuccessfully() {
        PlannerRegistry registry = new PlannerRegistry();
        registry.register(plannerFor(twoStepPlan()));

        PlanningRuntime runtime = new DefaultPlanningRuntime(registry);
        PlanningResult result = runtime.createPlan(PlanningRequest.of(decisionContext))
                .toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.plan().get().steps()).hasSize(2);
    }

    @Test
    void cyclicDependency_isRejected() {
        // Construct a plan with A -> B -> A cycle manually (bypassing PlanStep factory)
        PlanStepId idA = PlanStepId.generate();
        PlanStepId idB = PlanStepId.generate();

        PlanStep stepA = new PlanStep(idA, "Step A", Optional.empty(), List.of(idB), "", List.of(), Map.of());
        PlanStep stepB = new PlanStep(idB, "Step B", Optional.empty(), List.of(idA), "", List.of(), Map.of());

        AgentPlan cyclicPlan = new AgentPlan(
                PlanId.generate(), "exec-3", Optional.empty(), 0L,
                PlanStatus.PROPOSED, List.of(stepA, stepB), Instant.now(), Map.of()
        );

        PlannerRegistry registry = new PlannerRegistry();
        registry.register(plannerFor(cyclicPlan));

        PlanningRuntime runtime = new DefaultPlanningRuntime(registry);
        PlanningResult result = runtime.createPlan(PlanningRequest.of(decisionContext))
                .toCompletableFuture().join();

        assertThat(result.isFailure()).isTrue();
        assertThat(result.status()).isEqualTo(PlanningStatus.INVALID);
        assertThat(result.reason()).contains("Cyclic");
    }

    @Test
    void planRevision_preservesHistory() {
        AgentPlan original = singleStepPlan();
        PlanStep revisedStep = PlanStep.of("Updated step: more detailed research");
        AgentPlan revised = original.revise(List.of(revisedStep), "scope changed");

        PlannerRegistry registry = new PlannerRegistry();
        registry.register(new Planner() {
            @Override public String id() { return "revision-planner"; }
            @Override public CompletionStage<PlanningResult> plan(PlanningRequest req) {
                return CompletableFuture.completedFuture(PlanningResult.success(original));
            }
            @Override public CompletionStage<PlanningResult> revise(PlanRevisionRequest req) {
                return CompletableFuture.completedFuture(PlanningResult.success(revised));
            }
        });

        PlanningRuntime runtime = new DefaultPlanningRuntime(registry);
        PlanRevisionRequest revReq = new PlanRevisionRequest(original, decisionContext, "scope changed", Map.of());

        PlanningResult result = runtime.revisePlan(revReq).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.plan().get().revision()).isEqualTo(1L);
        assertThat(result.plan().get().metadata()).containsKey("previous_revision");
        assertThat(result.plan().get().metadata().get("revision_reason")).isEqualTo("scope changed");
    }

    @Test
    void plannerException_returnsFailedResult() {
        PlannerRegistry registry = new PlannerRegistry();
        registry.register(failingPlanner());

        PlanningRuntime runtime = new DefaultPlanningRuntime(registry);
        PlanningResult result = runtime.createPlan(PlanningRequest.of(decisionContext))
                .toCompletableFuture().join();

        assertThat(result.isFailure()).isTrue();
        assertThat(result.reason()).contains("planner exploded");
    }

    @Test
    void noPlanner_returnsFailedResult() {
        PlannerRegistry registry = new PlannerRegistry(); // empty

        PlanningRuntime runtime = new DefaultPlanningRuntime(registry);
        PlanningResult result = runtime.createPlan(PlanningRequest.of(decisionContext))
                .toCompletableFuture().join();

        assertThat(result.isFailure()).isTrue();
        assertThat(result.reason()).contains("No planner");
    }
}
