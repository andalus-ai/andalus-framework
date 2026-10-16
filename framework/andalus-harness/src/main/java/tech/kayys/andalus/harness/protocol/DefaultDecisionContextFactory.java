package tech.kayys.andalus.harness.protocol;

import tech.kayys.andalus.harness.context.ContextSnapshot;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Default implementation of {@link DecisionContextFactory}.
 */
public final class DefaultDecisionContextFactory implements DecisionContextFactory {

    @Override
    public CompletionStage<DecisionContext> create(
            AgentTurn turn,
            AgentTurnContext turnContext,
            ContextSnapshot context,
            List<TurnObservation> observations
    ) {
        Objects.requireNonNull(turnContext, "turnContext cannot be null");

        DecisionContext decisionContext = new DecisionContext(
                turnContext,
                Optional.empty(),
                Optional.empty(),
                DecisionConstraints.defaults(),
                observations != null ? observations : List.of(),
                Map.of()
        );

        return CompletableFuture.completedFuture(decisionContext);
    }
}
