package tech.kayys.andalus.harness.selection;

import java.util.concurrent.CompletionStage;

/**
 * Coordinates discovery, capability filtering, compatibility checking, and relevance assessment
 * to select suitable tool and/or skill candidates for a decision context.
 */
public interface ActionSelector {

    /**
     * Evaluates candidates for the given selection request.
     *
     * @param request the selection request
     * @return a completion stage resolving to a {@link SelectionResult}
     */
    CompletionStage<SelectionResult> select(SelectionRequest request);
}
