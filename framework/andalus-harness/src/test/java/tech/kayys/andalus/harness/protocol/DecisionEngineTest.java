package tech.kayys.andalus.harness.protocol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.context.ContextId;
import tech.kayys.andalus.harness.context.ContextSnapshot;
import tech.kayys.andalus.harness.context.DefaultAssembledContext;
import tech.kayys.andalus.harness.context.DefaultHarnessIdentity;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.tool.ToolArguments;
import tech.kayys.andalus.tool.resolution.ToolIntent;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class DecisionEngineTest {

    private AgentTurnContext mockTurnContext;

    @BeforeEach
    void setUp() {
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
                Map.of()
        );

        mockTurnContext = new AgentTurnContext() {
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
    void successfulDecision() {
        DecisionStrategy strategy = new DecisionStrategy() {
            @Override
            public String id() {
                return "test-strategy";
            }

            @Override
            public java.util.concurrent.CompletionStage<AgentDecision> decide(DecisionContext context) {
                return CompletableFuture.completedFuture(
                        DefaultAgentDecision.complete("Task finished", "result")
                );
            }
        };

        DecisionEngine engine = new DefaultDecisionEngine(strategy);
        DecisionContext context = DecisionContext.of(mockTurnContext);

        AgentDecision decision = engine.decide(context).toCompletableFuture().join();
        assertThat(decision).isNotNull();
        assertThat(decision.type()).isEqualTo(AgentDecisionType.COMPLETE);
    }

    @Test
    void rejectsDisallowedToolExecutionViaValidation() {
        // Constraints disallow tool use
        DecisionConstraints constraints = new DecisionConstraints(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                true,
                false, // allowToolUse = false
                true,
                true,
                true,
                Map.of()
        );

        DecisionStrategy strategy = new DecisionStrategy() {
            @Override
            public String id() {
                return "tool-strategy";
            }

            @Override
            public java.util.concurrent.CompletionStage<AgentDecision> decide(DecisionContext context) {
                return CompletableFuture.completedFuture(
                        DefaultAgentDecision.tool(ToolIntent.of("read_file", ToolArguments.empty(), null))
                );
            }
        };

        DecisionEngine engine = new DefaultDecisionEngine(strategy);
        DecisionContext context = DecisionContext.of(
                mockTurnContext,
                "Goal",
                "Task",
                constraints,
                List.of()
        );

        AgentDecision decision = engine.decide(context).toCompletableFuture().join();
        assertThat(decision.type()).isEqualTo(AgentDecisionType.FAIL);
        assertThat(decision.reason().orElse("")).contains("Tool execution is disabled by decision constraints");
    }

    @Test
    void handlesStrategyExceptionGracefully() {
        DecisionStrategy throwingStrategy = new DecisionStrategy() {
            @Override
            public String id() {
                return "throwing-strategy";
            }

            @Override
            public java.util.concurrent.CompletionStage<AgentDecision> decide(DecisionContext context) {
                throw new IllegalStateException("Intentional failure");
            }
        };

        DecisionEngine engine = new DefaultDecisionEngine(throwingStrategy);
        DecisionContext context = DecisionContext.of(mockTurnContext);

        AgentDecision decision = engine.decide(context).toCompletableFuture().join();
        assertThat(decision.type()).isEqualTo(AgentDecisionType.FAIL);
        assertThat(decision.reason().orElse("")).contains("Intentional failure");
    }

    @Test
    void fallbackStrategyWorksWhenPrimaryFails() {
        DecisionStrategy primary = new DecisionStrategy() {
            @Override
            public String id() {
                return "primary";
            }

            @Override
            public java.util.concurrent.CompletionStage<AgentDecision> decide(DecisionContext context) {
                CompletableFuture<AgentDecision> future = new CompletableFuture<>();
                future.completeExceptionally(new RuntimeException("Primary unavailable"));
                return future;
            }
        };

        DecisionStrategy fallback = new DecisionStrategy() {
            @Override
            public String id() {
                return "fallback";
            }

            @Override
            public java.util.concurrent.CompletionStage<AgentDecision> decide(DecisionContext context) {
                return CompletableFuture.completedFuture(
                        DefaultAgentDecision.complete("Fallback completed", "ok")
                );
            }
        };

        DecisionEngine engine = new DefaultDecisionEngine(primary, new DefaultDecisionValidator(), fallback);
        DecisionContext context = DecisionContext.of(mockTurnContext);

        AgentDecision decision = engine.decide(context).toCompletableFuture().join();
        assertThat(decision.type()).isEqualTo(AgentDecisionType.COMPLETE);
    }

    @Test
    void strategyRegistryOperations() {
        StrategyRegistry registry = new StrategyRegistry();
        DecisionStrategy strategy = new LegacyAgentStrategyAdapter("legacy-1", ctx -> DefaultAgentDecision.complete("done", null));

        registry.register(strategy);
        assertThat(registry.has("legacy-1")).isTrue();
        assertThat(registry.get("legacy-1")).contains(strategy);
        assertThat(registry.get("non-existent")).isEmpty();
    }
}
