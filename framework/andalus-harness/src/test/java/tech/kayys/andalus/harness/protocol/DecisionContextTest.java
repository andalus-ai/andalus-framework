package tech.kayys.andalus.harness.protocol;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.context.ContextId;
import tech.kayys.andalus.harness.context.ContextSnapshot;
import tech.kayys.andalus.harness.context.DefaultAssembledContext;
import tech.kayys.andalus.harness.context.DefaultHarnessIdentity;
import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DecisionContextTest {

    private AgentTurnContext createMockTurnContext() {
        ExecutionId executionId = ExecutionId.generate();
        DefaultAssembledContext assembled = new DefaultAssembledContext(
                ContextId.generate(),
                List.of(),
                Map.of(),
                0L
        );
        ContextSnapshot snapshot = new ContextSnapshot(
                "snap-1",
                Instant.now(),
                assembled,
                Map.of("key", "val")
        );

        return new AgentTurnContext() {
            @Override
            public ExecutionId executionId() {
                return executionId;
            }

            @Override
            public tech.kayys.andalus.harness.context.HarnessIdentity identity() {
                return DefaultHarnessIdentity.of("test-agent");
            }

            @Override
            public ContextSnapshot context() {
                return snapshot;
            }

            @Override
            public CapabilityView capabilities() {
                return null;
            }

            @Override
            public BudgetView budget() {
                return null;
            }

            @Override
            public ToolCatalogView tools() {
                return null;
            }

            @Override
            public ModelCatalogView models() {
                return null;
            }
        };
    }

    @Test
    void backwardCompatibleFactoriesWork() {
        AgentTurnContext tc = createMockTurnContext();
        DecisionContext dc = DecisionContext.of(tc);

        assertThat(dc.turnContext()).isSameAs(tc);
        assertThat(dc.executionId()).isEqualTo(tc.executionId().value());
        assertThat(dc.context()).isSameAs(tc.context());
        assertThat(dc.observations()).isEmpty();
        assertThat(dc.attributes()).isEmpty();
        assertThat(dc.goal()).isEmpty();
        assertThat(dc.task()).isEmpty();
        assertThat(dc.constraints()).isNotNull();
        assertThat(dc.constraints().allowToolUse()).isTrue();
    }

    @Test
    void threeArgConstructorRemainsBackwardCompatible() {
        AgentTurnContext tc = createMockTurnContext();
        TurnObservation obs = TurnObservation.of(
                TurnId.generate(),
                ObservationType.MODEL_OUTPUT,
                "Execution finished"
        );
        DecisionContext dc = new DecisionContext(tc, List.of(obs), Map.of("step", 1));

        assertThat(dc.observations()).hasSize(1);
        assertThat(dc.attributes()).containsEntry("step", 1);
        assertThat(dc.goal()).isEmpty();
        assertThat(dc.task()).isEmpty();
    }

    @Test
    void richDecisionContextAndFactory() {
        AgentTurnContext tc = createMockTurnContext();
        DecisionConstraints constraints = new DecisionConstraints(
                Optional.of(5),
                Optional.of(Duration.ofSeconds(30)),
                Optional.of(2),
                true,
                true,
                false,
                true,
                true,
                Map.of("custom", "val")
        );

        DecisionContext dc = DecisionContext.of(
                tc,
                "Analyze inflation trends",
                "Query knowledge source",
                constraints,
                List.of()
        );

        assertThat(dc.goal()).contains("Analyze inflation trends");
        assertThat(dc.task()).contains("Query knowledge source");
        assertThat(dc.constraints().maxActions()).contains(5);
        assertThat(dc.constraints().allowSkillUse()).isFalse();

        // Factory test
        DecisionContextFactory factory = new DefaultDecisionContextFactory();
        AgentTurn turn = AgentTurn.legacy(
                TurnId.generate(),
                tc.executionId(),
                DefaultAgentDecision.complete("done", "result"),
                TurnOutcome.success("done"),
                Instant.now()
        );

        DecisionContext created = factory.create(turn, tc, tc.context(), List.of()).toCompletableFuture().join();
        assertThat(created).isNotNull();
        assertThat(created.turnContext()).isSameAs(tc);
    }

    @Test
    void nullValidation() {
        assertThatThrownBy(() -> new DecisionContext(null, List.of(), Map.of()))
                .isInstanceOf(NullPointerException.class);
    }
}
