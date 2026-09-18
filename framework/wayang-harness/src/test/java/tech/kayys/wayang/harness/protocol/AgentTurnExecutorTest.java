package tech.kayys.wayang.harness.protocol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.capability.DefaultCapabilityScope;
import tech.kayys.wayang.harness.context.DefaultHarnessIdentity;
import tech.kayys.wayang.harness.execution.action.InMemoryActionJournal;
import tech.kayys.wayang.harness.execution.checkpoint.CheckpointId;
import tech.kayys.wayang.harness.execution.checkpoint.CheckpointManager;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.execution.state.ExecutionState;
import tech.kayys.wayang.harness.governance.budget.DefaultBudgetLedger;
import tech.kayys.wayang.harness.governance.budget.DefaultBudgetManager;
import tech.kayys.wayang.harness.governance.budget.DefaultBudgetPolicy;
import tech.kayys.wayang.harness.governance.policy.AllowDecision;
import tech.kayys.wayang.harness.memory.InMemoryMemoryStore;
import tech.kayys.wayang.harness.model.*;
import tech.kayys.wayang.harness.tool.GovernedToolExecutor;
import tech.kayys.wayang.tool.*;
import tech.kayys.wayang.tool.catalog.ToolCatalog;
import tech.kayys.wayang.tool.resolution.DefaultToolResolver;
import tech.kayys.wayang.tool.resolution.ToolIntent;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AgentTurnExecutorTest {

    private AgentTurnExecutor turnExecutor;
    private CheckpointManager mockCheckpointManager;

    @BeforeEach
    void setUp() {
        // Setup mock model executor
        ModelCatalog modelCatalog = ModelCatalog.create();
        ModelDescriptor mDesc = DefaultModelDescriptor.builder(ModelId.of("gpt"), "GPT").build();
        modelCatalog.register(new ModelProvider() {
            @Override public Collection<ModelDescriptor> models() { return List.of(mDesc); }
            @Override public Optional<ModelDescriptor> describe(ModelId id) { return Optional.of(mDesc); }
            @Override public ModelExecutor executor(ModelId id) {
                return (inv, ctx) -> ModelResult.success(inv.id(), "thought completed", ModelUsage.empty(), mDesc.metadata());
            }
        });
        DefaultBudgetLedger ledger = new DefaultBudgetLedger();
        DefaultBudgetManager budgetManager = new DefaultBudgetManager(ledger, new DefaultBudgetPolicy(ledger));
        GovernedModelExecutor modelExecutor = new GovernedModelExecutor(
                modelCatalog,
                new DefaultModelRouter(modelCatalog),
                DefaultCapabilityScope.allowAll(),
                (ctx, act) -> AllowDecision.of("p", "allow"),
                budgetManager,
                new InMemoryActionJournal()
        );

        // Setup mock tool executor
        ToolCatalog toolCatalog = ToolCatalog.create();
        ToolDescriptor tDesc = DefaultToolDescriptor.builder(ToolId.of("bash"), "Bash")
                .capabilities(Set.of("code.execute"))
                .build();
        toolCatalog.register(new ToolProvider() {
            @Override public Optional<ToolDescriptor> describe(ToolId id) { return Optional.of(tDesc); }
            @Override public Collection<ToolDescriptor> tools() { return List.of(tDesc); }
            @Override public ToolExecutor executor(ToolId id) {
                return ToolExecutor.synchronous((inv, ctx) -> ToolResult.success(inv.invocationIdentifier(), "echo done", Duration.ZERO, "bash"));
            }
        });
        GovernedToolExecutor toolExec = new GovernedToolExecutor(
                toolCatalog,
                new DefaultToolResolver(toolCatalog),
                DefaultCapabilityScope.allowAll(),
                (ctx, act) -> AllowDecision.of("p", "allow"),
                budgetManager,
                null,
                new InMemoryActionJournal(),
                new InMemoryMemoryStore(),
                null, null, null, null
        );

        mockCheckpointManager = new CheckpointManager() {
            @Override public CheckpointId create(ExecutionState state) { return CheckpointId.generate(); }
            @Override public Optional<tech.kayys.wayang.harness.execution.checkpoint.Checkpoint> load(CheckpointId id) { return Optional.empty(); }
            @Override public Optional<tech.kayys.wayang.harness.execution.checkpoint.Checkpoint> latest(ExecutionId executionId) { return Optional.empty(); }
            @Override public void delete(CheckpointId id) {}
        };

        turnExecutor = new AgentTurnExecutor(modelExecutor, toolExec, mockCheckpointManager);
    }

    @Test
    void shouldExecuteInferenceTurn() {
        AgentTurnContext ctx = new DefaultAgentTurnContext(null, DefaultHarnessIdentity.of("agent-1"), null, null, null, null, null);
        AgentDecision decision = DefaultAgentDecision.infer(ModelIntent.prompt("What next?"));

        AgentTurn turn = turnExecutor.executeTurn(ctx, decision, null, null);

        assertThat(turn.outcome().successful()).isTrue();
        assertThat(turn.decision().type()).isEqualTo(AgentDecisionType.INFER);
        assertThat(turn.outcome().result()).isInstanceOf(ModelResult.class);
    }

    @Test
    void shouldExecuteToolTurn() {
        AgentTurnContext ctx = new DefaultAgentTurnContext(null, DefaultHarnessIdentity.of("agent-1"), null, null, null, null, null);
        AgentDecision decision = DefaultAgentDecision.tool(ToolIntent.of("code.execute", ToolArguments.empty(), null));

        AgentTurn turn = turnExecutor.executeTurn(ctx, decision, null, null);

        assertThat(turn.outcome().successful()).isTrue();
        assertThat(turn.decision().type()).isEqualTo(AgentDecisionType.EXECUTE_TOOL);
        assertThat(turn.outcome().result()).isInstanceOf(ToolResult.class);
    }

    @Test
    void shouldExecuteCheckpointTurn() {
        AgentTurnContext ctx = new DefaultAgentTurnContext(null, DefaultHarnessIdentity.of("agent-1"), null, null, null, null, null);
        AgentDecision decision = DefaultAgentDecision.checkpoint("step-1");

        AgentTurn turn = turnExecutor.executeTurn(ctx, decision, null, null);

        assertThat(turn.outcome().successful()).isTrue();
        assertThat(turn.outcome().result()).isInstanceOf(CheckpointId.class);
    }

    @Test
    void shouldExecuteCompletionTurn() {
        AgentTurnContext ctx = new DefaultAgentTurnContext(null, DefaultHarnessIdentity.of("agent-1"), null, null, null, null, null);
        AgentDecision decision = DefaultAgentDecision.complete("Task finished successfully", 100);

        AgentTurn turn = turnExecutor.executeTurn(ctx, decision, null, null);

        assertThat(turn.outcome().successful()).isTrue();
        assertThat(turn.outcome().result()).isInstanceOf(AgentOutput.class);
        AgentOutput output = (AgentOutput) turn.outcome().result();
        assertThat(output.summary()).isEqualTo("Task finished successfully");
    }

    @Test
    void shouldExecuteFailureTurn() {
        AgentTurnContext ctx = new DefaultAgentTurnContext(null, DefaultHarnessIdentity.of("agent-1"), null, null, null, null, null);
        AgentDecision decision = DefaultAgentDecision.fail("Goal is unreachable");

        AgentTurn turn = turnExecutor.executeTurn(ctx, decision, null, null);

        assertThat(turn.outcome().successful()).isFalse();
        assertThat(turn.outcome().error()).contains("Goal is unreachable");
    }
}
