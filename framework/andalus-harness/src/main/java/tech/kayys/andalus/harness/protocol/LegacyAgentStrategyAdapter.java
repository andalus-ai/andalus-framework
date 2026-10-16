package tech.kayys.andalus.harness.protocol;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * Compatibility adapter wrapping a synchronous or functional strategy into a {@link DecisionStrategy}.
 */
public final class LegacyAgentStrategyAdapter implements DecisionStrategy {

    private final String id;
    private final Function<DecisionContext, AgentDecision> decisionFunction;

    public LegacyAgentStrategyAdapter(String id, Function<DecisionContext, AgentDecision> decisionFunction) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.decisionFunction = Objects.requireNonNull(decisionFunction, "decisionFunction cannot be null");
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public CompletionStage<AgentDecision> decide(DecisionContext context) {
        try {
            AgentDecision decision = decisionFunction.apply(context);
            if (decision == null) {
                return CompletableFuture.completedFuture(
                        AgentDecision.fail("Legacy strategy returned null decision")
                );
            }
            return CompletableFuture.completedFuture(decision);
        } catch (RuntimeException ex) {
            return CompletableFuture.completedFuture(
                    AgentDecision.fail("Legacy strategy failed: " + ex.getClass().getSimpleName() + ": " + ex.getMessage())
            );
        }
    }
}
