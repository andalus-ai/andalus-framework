package tech.kayys.andalus.harness.protocol;

import tech.kayys.andalus.harness.context.ContextSnapshot;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Factory SPI responsible for assembling a {@link DecisionContext} for an agent turn.
 */
public interface DecisionContextFactory {

    CompletionStage<DecisionContext> create(
            AgentTurn turn,
            AgentTurnContext turnContext,
            ContextSnapshot context,
            List<TurnObservation> observations
    );
}
