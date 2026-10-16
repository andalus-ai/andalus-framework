package tech.kayys.andalus.harness.selection;

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
import tech.kayys.andalus.tool.ToolDescriptor;
import tech.kayys.andalus.tool.ToolId;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class ActionSelectorTest {

    private DecisionContext decisionContext;
    private ToolCandidateSource toolSource;
    private SkillCandidateSource skillSource;

    @BeforeEach
    void setUp() {
        ExecutionId executionId = ExecutionId.generate();
        DefaultAssembledContext assembled = new DefaultAssembledContext(
                ContextId.generate(), List.of(), Map.of(), 0L);
        ContextSnapshot snapshot = new ContextSnapshot("snap-s", Instant.now(), assembled, Map.of());

        AgentTurnContext turnContext = new AgentTurnContext() {
            @Override public ExecutionId executionId() { return executionId; }
            @Override public tech.kayys.andalus.harness.context.HarnessIdentity identity() {
                return DefaultHarnessIdentity.of("selector-test-agent");
            }
            @Override public ContextSnapshot context() { return snapshot; }
            @Override public CapabilityView capabilities() { return CapabilityView.of(Set.of("fs.read", "web.search")); }
            @Override public BudgetView budget() { return null; }
            @Override public ToolCatalogView tools() { return null; }
            @Override public ModelCatalogView models() { return null; }
        };

        decisionContext = DecisionContext.of(turnContext);

        toolSource = new ToolCandidateSource();
        skillSource = new SkillCandidateSource();
    }

    @Test
    void singleCompatibleTool_isSelected() {
        CandidateDescriptor tool = new CandidateDescriptor(
                "file-reader",
                SelectionTarget.TOOL,
                Set.of("fs.read"),
                "Reads files from the workspace",
                Map.of(),
                true,
                Map.of()
        );
        toolSource.registerTool(tool);

        ActionSelector selector = new DefaultActionSelector(toolSource, skillSource);
        SelectionRequest request = SelectionRequest.forCapability(decisionContext, SelectionTarget.TOOL, "fs.read");

        SelectionResult result = selector.select(request).toCompletableFuture().join();

        assertThat(result.status()).isEqualTo(SelectionStatus.CANDIDATES_FOUND);
        assertThat(result.candidates()).hasSize(1);
        assertThat(result.candidates().get(0).candidateId()).isEqualTo("file-reader");
        assertThat(result.candidates().get(0).target()).isEqualTo(SelectionTarget.TOOL);
        assertThat(result.exclusions()).isEmpty();
    }

    @Test
    void missingCapability_returnsCapabilityUnavailable() {
        CandidateDescriptor tool = new CandidateDescriptor(
                "file-reader",
                SelectionTarget.TOOL,
                Set.of("fs.read"),
                "Reads files",
                Map.of(),
                true,
                Map.of()
        );
        toolSource.registerTool(tool);

        ActionSelector selector = new DefaultActionSelector(toolSource);
        SelectionRequest request = SelectionRequest.forCapability(decisionContext, SelectionTarget.TOOL, "db.query");

        SelectionResult result = selector.select(request).toCompletableFuture().join();

        assertThat(result.status()).isEqualTo(SelectionStatus.CAPABILITY_UNAVAILABLE);
        assertThat(result.candidates()).isEmpty();
        assertThat(result.exclusions()).hasSize(1);
        assertThat(result.exclusions().get(0).reason()).isEqualTo(ExclusionReason.CAPABILITY_MISMATCH);
    }

    @Test
    void disabledCandidate_isExcluded() {
        CandidateDescriptor disabledTool = new CandidateDescriptor(
                "disabled-calc",
                SelectionTarget.TOOL,
                Set.of("math.eval"),
                "Calculates math",
                Map.of(),
                false, // disabled
                Map.of()
        );
        toolSource.registerTool(disabledTool);

        ActionSelector selector = new DefaultActionSelector(toolSource);
        SelectionRequest request = SelectionRequest.of(decisionContext, SelectionTarget.TOOL);

        SelectionResult result = selector.select(request).toCompletableFuture().join();

        assertThat(result.status()).isEqualTo(SelectionStatus.NO_CANDIDATES);
        assertThat(result.exclusions()).hasSize(1);
        assertThat(result.exclusions().get(0).reason()).isEqualTo(ExclusionReason.DISABLED);
    }

    @Test
    void incompatibleInputSchema_isExcluded() {
        CandidateDescriptor toolWithSchema = new CandidateDescriptor(
                "weather-tool",
                SelectionTarget.TOOL,
                Set.of("weather.query"),
                "Gets weather",
                Map.of("city", "string"),
                true,
                Map.of()
        );
        toolSource.registerTool(toolWithSchema);

        ActionSelector selector = new DefaultActionSelector(toolSource);
        // Request requires "coordinates" which the tool doesn't have in its input schema
        SelectionRequest request = new SelectionRequest(
                decisionContext,
                SelectionTarget.TOOL,
                Optional.empty(),
                Map.of("coordinates", Map.of("lat", 0.0, "lon", 0.0)),
                Map.of()
        );

        SelectionResult result = selector.select(request).toCompletableFuture().join();

        assertThat(result.status()).isEqualTo(SelectionStatus.NO_CANDIDATES);
        assertThat(result.exclusions()).hasSize(1);
        assertThat(result.exclusions().get(0).reason()).isEqualTo(ExclusionReason.INPUT_SCHEMA_MISMATCH);
    }

    @Test
    void mixedSelection_selectsBothToolsAndSkills() {
        CandidateDescriptor tool = CandidateDescriptor.ofTool("web-search", Set.of("search"), "Web search");
        CandidateDescriptor skill = CandidateDescriptor.ofSkill("research-skill", Set.of("research"), "Research workflow");

        toolSource.registerTool(tool);
        skillSource.registerSkill(skill);

        ActionSelector selector = new DefaultActionSelector(toolSource, skillSource);
        SelectionRequest request = SelectionRequest.of(decisionContext, SelectionTarget.TOOL_OR_SKILL);

        SelectionResult result = selector.select(request).toCompletableFuture().join();

        assertThat(result.status()).isEqualTo(SelectionStatus.CANDIDATES_FOUND);
        assertThat(result.candidates()).hasSize(2);
        assertThat(result.candidates()).extracting(SelectionCandidate::candidateId)
                .containsExactlyInAnyOrder("web-search", "research-skill");
    }

    @Test
    void targetFiltering_excludesOtherTargets() {
        CandidateDescriptor tool = CandidateDescriptor.ofTool("web-search", Set.of("search"), "Web search");
        CandidateDescriptor skill = CandidateDescriptor.ofSkill("research-skill", Set.of("research"), "Research workflow");

        toolSource.registerTool(tool);
        skillSource.registerSkill(skill);

        ActionSelector selector = new DefaultActionSelector(toolSource, skillSource);
        // Request SKILL only
        SelectionRequest request = SelectionRequest.of(decisionContext, SelectionTarget.SKILL);

        SelectionResult result = selector.select(request).toCompletableFuture().join();

        assertThat(result.status()).isEqualTo(SelectionStatus.CANDIDATES_FOUND);
        assertThat(result.candidates()).hasSize(1);
        assertThat(result.candidates().get(0).candidateId()).isEqualTo("research-skill");
    }

    @Test
    void sourceException_returnsFailedResult() {
        CandidateSource failingSource = new CandidateSource() {
            @Override public SelectionTarget supportedTarget() { return SelectionTarget.TOOL; }
            @Override public CompletableFuture<List<CandidateDescriptor>> discover(SelectionRequest request) {
                CompletableFuture<List<CandidateDescriptor>> future = new CompletableFuture<>();
                future.completeExceptionally(new RuntimeException("Discovery failed"));
                return future;
            }
        };

        ActionSelector selector = new DefaultActionSelector(failingSource);
        SelectionRequest request = SelectionRequest.of(decisionContext, SelectionTarget.TOOL);

        SelectionResult result = selector.select(request).toCompletableFuture().join();

        assertThat(result.status()).isEqualTo(SelectionStatus.FAILED);
        assertThat(result.diagnostics()).containsKey("reason");
        assertThat((String) result.diagnostics().get("reason")).contains("Discovery failed");
    }
}
