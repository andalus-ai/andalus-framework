package tech.kayys.andalus.harness.inference;

import java.util.concurrent.CompletionStage;

/**
 * Primary runtime contract for executing model inferences.
 */
public interface InferenceRuntime {

    /**
     * Executes an inference request under its planned model, provider, and constraints.
     *
     * @param request the planned inference request
     * @return a completion stage resolving to an {@link InferenceResult}
     */
    CompletionStage<InferenceResult> execute(InferenceRequest request);
}
