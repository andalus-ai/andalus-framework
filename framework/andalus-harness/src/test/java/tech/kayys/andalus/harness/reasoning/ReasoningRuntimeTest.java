package tech.kayys.andalus.harness.reasoning;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.assertj.core.api.Assertions.assertThat;

class ReasoningRuntimeTest {

    private DecisionContext decisionContext;

    @BeforeEach
    void setUp() {
        ExecutionId executionId = ExecutionId.generate();
        DefaultAssembledContext assembled = new DefaultAssembledContext(
                ContextId.generate(), List.of(), Map.of(), 0L);
        ContextSnapshot snapshot = new ContextSnapshot("snap-r", Instant.now(), assembled, Map.of());

        AgentTurnContext turnContext = new AgentTurnContext() {
            @Override public ExecutionId executionId() { return executionId; }
            @Override public tech.kayys.andalus.harness.context.HarnessIdentity identity() {
                return DefaultHarnessIdentity.of("reasoning-test-agent");
            }
            @Override public ContextSnapshot context() { return snapshot; }
            @Override public CapabilityView capabilities() { return null; }
            @Override public BudgetView budget() { return null; }
            @Override public ToolCatalogView tools() { return null; }
            @Override public ModelCatalogView models() { return null; }
        };
        decisionContext = DecisionContext.of(turnContext);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private ContextAwareReasoner asyncReasoner(String id, String conclusion) {
        return new ContextAwareReasoner() {
            @Override public String id() { return id; }
            @Override public CompletionStage<ReasoningAssessment> reason(ReasoningRequest req) {
                return CompletableFuture.completedFuture(
                        ReasoningAssessment.of(req.purpose(), conclusion));
            }
        };
    }

    private ContextAwareReasoner throwingReasoner() {
        return new ContextAwareReasoner() {
            @Override public String id() { return "throwing"; }
            @Override public CompletionStage<ReasoningAssessment> reason(ReasoningRequest req) {
                CompletableFuture<ReasoningAssessment> f = new CompletableFuture<>();
                f.completeExceptionally(new RuntimeException("reasoner exploded"));
                return f;
            }
        };
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    @Test
    void successfulReasoning_returnsAssessment() {
        ReasonerRegistry registry = new ReasonerRegistry();
        registry.register(asyncReasoner("cot", "The plan is plausible."));

        ReasoningRuntime runtime = new DefaultReasoningRuntime(registry);
        ReasoningRequest req = ReasoningRequest.of(decisionContext, ReasoningPurpose.INTERPRET_EVIDENCE);

        ReasoningAssessment result = runtime.reason(req).toCompletableFuture().join();

        assertThat(result).isNotNull();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.conclusion()).isEqualTo("The plan is plausible.");
        assertThat(result.purpose()).isEqualTo(ReasoningPurpose.INTERPRET_EVIDENCE);
    }

    @Test
    void legacyAdapterReasoning_worksCorrectly() {
        ReasonerRegistry registry = new ReasonerRegistry();
        registry.register(new LegacyReasonerAdapter(
                "legacy", ReasoningPurpose.EVALUATE_PLAN,
                req -> ReasoningAssessment.of(req.purpose(), "Plan has no cycles.")));

        ReasoningRuntime runtime = new DefaultReasoningRuntime(registry);
        ReasoningRequest req = ReasoningRequest.of(decisionContext, ReasoningPurpose.EVALUATE_PLAN);

        ReasoningAssessment result = runtime.reason(req).toCompletableFuture().join();

        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.conclusion()).isEqualTo("Plan has no cycles.");
    }

    @Test
    void noProvider_returnsFailedAssessment() {
        ReasonerRegistry registry = new ReasonerRegistry();  // empty

        ReasoningRuntime runtime = new DefaultReasoningRuntime(registry);
        ReasoningRequest req = ReasoningRequest.of(decisionContext, ReasoningPurpose.IDENTIFY_UNCERTAINTY);

        ReasoningAssessment result = runtime.reason(req).toCompletableFuture().join();

        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.conclusion()).contains("No reasoning provider");
    }

    @Test
    void providerException_returnsControlledFailure() {
        ReasonerRegistry registry = new ReasonerRegistry();
        registry.register(throwingReasoner());

        ReasoningRuntime runtime = new DefaultReasoningRuntime(registry);
        ReasoningRequest req = ReasoningRequest.of(decisionContext, ReasoningPurpose.ASSESS_ALTERNATIVES);

        ReasoningAssessment result = runtime.reason(req).toCompletableFuture().join();

        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.conclusion()).contains("reasoner exploded");
    }

    @Test
    void purposeFiltering_selectsCorrectProvider() {
        ReasonerRegistry registry = new ReasonerRegistry();
        // Only supports EVALUATE_PLAN
        registry.register(new LegacyReasonerAdapter(
                "plan-only", ReasoningPurpose.EVALUATE_PLAN,
                req -> ReasoningAssessment.of(req.purpose(), "Plan evaluation done.")));

        ReasoningRuntime runtime = new DefaultReasoningRuntime(registry);

        // Should succeed for EVALUATE_PLAN
        ReasoningAssessment ok = runtime.reason(
                ReasoningRequest.of(decisionContext, ReasoningPurpose.EVALUATE_PLAN))
                .toCompletableFuture().join();
        assertThat(ok.isSuccessful()).isTrue();

        // Should fail for a different purpose (no matching provider)
        ReasoningAssessment fail = runtime.reason(
                ReasoningRequest.of(decisionContext, ReasoningPurpose.EXPLAIN_DECISION))
                .toCompletableFuture().join();
        assertThat(fail.isSuccessful()).isFalse();
        assertThat(fail.conclusion()).contains("No reasoning provider");
    }

    @Test
    void assessmentWithAlternativesAndConfidence_roundTrips() {
        ReasoningAssessment base = ReasoningAssessment.of(
                ReasoningPurpose.ASSESS_ALTERNATIVES, "Option A is preferred.");
        ReasoningAssessment enriched = base
                .withAlternative("Option B is possible but riskier.")
                .withConfidence(0.75);

        assertThat(enriched.alternatives()).contains("Option B is possible but riskier.");
        assertThat(enriched.confidence()).isEqualTo(0.75);
        assertThat(enriched.purpose()).isEqualTo(ReasoningPurpose.ASSESS_ALTERNATIVES);
        assertThat(enriched.isSuccessful()).isTrue();
    }

    @Test
    void resultValidator_rejectsBlankConclusion() {
        ReasoningResultValidator validator = new ReasoningResultValidator();
        ReasoningAssessment bad = new ReasoningAssessment(
                "id-1", ReasoningPurpose.INTERPRET_EVIDENCE,
                "", List.of(), List.of(), Map.of(), Map.of(),
                0.5, 0L, Instant.now());

        List<String> errors = validator.validate(bad);
        assertThat(errors).anyMatch(e -> e.contains("non-blank conclusion"));
    }
}
