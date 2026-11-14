package tech.kayys.andalus.harness.inference;

import java.util.concurrent.CompletionStage;

/**
 * SPI for backend model providers (e.g. Aqli, local engine, cloud endpoints)
 * executing concrete {@link InferenceRequest}s.
 */
public interface InferenceProviderAdapter {

    /**
     * Unique identifier of this provider (e.g., "aqli", "openai", "local").
     */
    String providerId();

    /**
     * Determines whether this provider can serve the specified model.
     *
     * @param modelId model identifier
     * @return true if supported
     */
    boolean supports(String modelId);

    /**
     * Executes the given inference request.
     *
     * @param request the inference request
     * @return completion stage resolving to an {@link InferenceResult}
     */
    CompletionStage<InferenceResult> execute(InferenceRequest request);
}
