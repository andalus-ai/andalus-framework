package tech.kayys.andalus.harness.inference.planning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.inference.InferenceConstraints;
import tech.kayys.andalus.harness.inference.InferenceInputRequirements;
import tech.kayys.andalus.harness.inference.InferenceOutputRequirements;
import tech.kayys.andalus.harness.inference.InferenceRequirement;
import tech.kayys.andalus.harness.inference.InferenceTask;
import tech.kayys.andalus.harness.inference.OutputFormat;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class InferencePlannerTest {

    private DefaultInferencePlanner planner;

    @BeforeEach
    void setUp() {
        planner = new DefaultInferencePlanner(PlanningStrategy.DIRECT);
    }

    @Test
    void directStrategy_withAvailableRequestedModel_selectsModel() {
        InferenceCandidate candidate = InferenceCandidate.local("llama-3-8b", "ollama", Set.of("text-generation"));
        planner.registerCandidate(candidate);

        InferenceRequirement req = new InferenceRequirement(
                InferenceTask.GENERATION,
                Set.of("text-generation"),
                Optional.of("llama-3-8b"),
                Optional.empty(),
                InferenceInputRequirements.empty(),
                InferenceOutputRequirements.text(),
                InferenceConstraints.defaults(),
                Map.of()
        );

        InferencePlanningResult result = planner.plan(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.plan()).isPresent();
        assertThat(result.plan().get().modelId()).isEqualTo("llama-3-8b");
        assertThat(result.plan().get().strategy()).isEqualTo("DIRECT");
    }

    @Test
    void directStrategy_withUnregisteredRequestedModel_returnsModelUnavailable() {
        InferenceRequirement req = new InferenceRequirement(
                InferenceTask.GENERATION,
                Set.of("text-generation"),
                Optional.of("nonexistent-model"),
                Optional.empty(),
                InferenceInputRequirements.empty(),
                InferenceOutputRequirements.text(),
                InferenceConstraints.defaults(),
                Map.of()
        );

        InferencePlanningResult result = planner.plan(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.status()).isEqualTo(InferencePlanningStatus.MODEL_UNAVAILABLE);
    }

    @Test
    void directStrategy_withInfeasibleRequestedModel_returnsConstraintConflict() {
        // Model doesn't have the required capability
        InferenceCandidate candidate = InferenceCandidate.local("llama-3-8b", "ollama", Set.of("classification"));
        planner.registerCandidate(candidate);

        InferenceRequirement req = new InferenceRequirement(
                InferenceTask.GENERATION,
                Set.of("text-generation"), // requires text-generation
                Optional.of("llama-3-8b"),
                Optional.empty(),
                InferenceInputRequirements.empty(),
                InferenceOutputRequirements.text(),
                InferenceConstraints.defaults(),
                Map.of()
        );

        InferencePlanningResult result = planner.plan(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.status()).isEqualTo(InferencePlanningStatus.CONSTRAINT_CONFLICT);
        assertThat(result.exclusions()).hasSize(1);
        assertThat(result.exclusions().get(0).reason()).isEqualTo("CAPABILITY_MISMATCH");
    }

    @Test
    void adaptiveStrategy_selectsLowestLatencyCandidate() {
        InferenceCandidate fastCandidate = new InferenceCandidate(
                "provider1:fast-model",
                "fast-model",
                "provider1",
                false,
                Set.of("reasoning"),
                InferenceCandidateLimits.standard(8192, 2048),
                InferenceCandidateTelemetry.of(200, 1000), // 200ms latency
                Map.of()
        );

        InferenceCandidate slowCandidate = new InferenceCandidate(
                "provider2:slow-model",
                "slow-model",
                "provider2",
                false,
                Set.of("reasoning"),
                InferenceCandidateLimits.standard(8192, 2048),
                InferenceCandidateTelemetry.of(800, 500), // 800ms latency
                Map.of()
        );

        DefaultInferencePlanner adaptivePlanner = new DefaultInferencePlanner(PlanningStrategy.ADAPTIVE);
        adaptivePlanner.registerCandidate(fastCandidate);
        adaptivePlanner.registerCandidate(slowCandidate);

        InferenceRequirement req = new InferenceRequirement(
                InferenceTask.REASONING,
                Set.of("reasoning"),
                Optional.empty(),
                Optional.empty(),
                InferenceInputRequirements.empty(),
                InferenceOutputRequirements.text(),
                InferenceConstraints.defaults(),
                Map.of()
        );

        InferencePlanningResult result = adaptivePlanner.plan(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.plan().get().modelId()).isEqualTo("fast-model");
        assertThat(result.plan().get().strategy()).isEqualTo("ADAPTIVE");
    }

    @Test
    void structuredOutputFormat_excludesUnsupportedCandidates() {
        InferenceCandidate basicModel = new InferenceCandidate(
                "prov:basic",
                "basic-model",
                "prov",
                true,
                Set.of("extraction"),
                InferenceCandidateLimits.basic(4096, 1024), // supportsStructuredOutput = false
                InferenceCandidateTelemetry.empty(),
                Map.of()
        );

        DefaultInferencePlanner adaptivePlanner = new DefaultInferencePlanner(PlanningStrategy.ADAPTIVE);
        adaptivePlanner.registerCandidate(basicModel);

        InferenceRequirement req = new InferenceRequirement(
                InferenceTask.EXTRACTION,
                Set.of("extraction"),
                Optional.empty(),
                Optional.empty(),
                InferenceInputRequirements.empty(),
                InferenceOutputRequirements.json("{}"), // JSON requires structured output support
                InferenceConstraints.defaults(),
                Map.of()
        );

        InferencePlanningResult result = adaptivePlanner.plan(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.status()).isEqualTo(InferencePlanningStatus.NO_FEASIBLE_CANDIDATE);
        assertThat(result.exclusions()).hasSize(1);
        assertThat(result.exclusions().get(0).reason()).isEqualTo("UNSUPPORTED_OUTPUT_FORMAT");
    }

    @Test
    void localOnlyDeployment_excludesRemoteCandidates() {
        InferenceCandidate cloudCandidate = InferenceCandidate.cloud("gpt-4o", "openai", Set.of("text-generation"));
        InferenceCandidate localCandidate = InferenceCandidate.local("mistral-7b", "ollama", Set.of("text-generation"));

        DefaultInferencePlanner localPlanner = new DefaultInferencePlanner(
                java.util.List.of(cloudCandidate, localCandidate),
                PlanningStrategy.ADAPTIVE,
                DeploymentMode.LOCAL_ONLY
        );

        InferenceRequirement req = new InferenceRequirement(
                InferenceTask.GENERATION,
                Set.of("text-generation"),
                Optional.empty(),
                Optional.empty(),
                InferenceInputRequirements.empty(),
                InferenceOutputRequirements.text(),
                InferenceConstraints.defaults(),
                Map.of()
        );

        InferencePlanningResult result = localPlanner.plan(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.plan().get().modelId()).isEqualTo("mistral-7b");
        assertThat(result.exclusions()).extracting(InferenceCandidateExclusion::candidateId)
                .contains("openai:gpt-4o");
    }

    @Test
    void stableTieBreaking_producesPredictableSelection() {
        // Two candidates with identical latency and cost
        InferenceCandidate candidateB = new InferenceCandidate(
                "provider:model-b",
                "model-b",
                "provider",
                true,
                Set.of("generation"),
                InferenceCandidateLimits.standard(8192, 2048),
                InferenceCandidateTelemetry.of(300, 100),
                Map.of()
        );
        InferenceCandidate candidateA = new InferenceCandidate(
                "provider:model-a",
                "model-a",
                "provider",
                true,
                Set.of("generation"),
                InferenceCandidateLimits.standard(8192, 2048),
                InferenceCandidateTelemetry.of(300, 100),
                Map.of()
        );

        DefaultInferencePlanner adaptivePlanner = new DefaultInferencePlanner(PlanningStrategy.ADAPTIVE);
        adaptivePlanner.registerCandidate(candidateB);
        adaptivePlanner.registerCandidate(candidateA);

        InferenceRequirement req = new InferenceRequirement(
                InferenceTask.GENERATION,
                Set.of("generation"),
                Optional.empty(),
                Optional.empty(),
                InferenceInputRequirements.empty(),
                InferenceOutputRequirements.text(),
                InferenceConstraints.defaults(),
                Map.of()
        );

        InferencePlanningResult result = adaptivePlanner.plan(req).toCompletableFuture().join();

        assertThat(result.isSuccess()).isTrue();
        // CandidateId "provider:model-a" comes before "provider:model-b" lexicographically
        assertThat(result.plan().get().modelId()).isEqualTo("model-a");
    }
}
