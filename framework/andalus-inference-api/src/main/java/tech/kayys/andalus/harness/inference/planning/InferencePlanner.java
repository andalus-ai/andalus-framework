package tech.kayys.andalus.harness.inference.planning;

import tech.kayys.andalus.harness.inference.InferenceRequirement;

import java.util.concurrent.CompletionStage;

/**
 * Primary SPI for planning how an {@link InferenceRequirement} will be executed.
 */
public interface InferencePlanner {

    /**
     * Produces an {@link InferencePlanningResult} for the given requirement.
     *
     * @param requirement declarative inference requirement
     * @return completion stage resolving to planning result
     */
    CompletionStage<InferencePlanningResult> plan(InferenceRequirement requirement);
}
