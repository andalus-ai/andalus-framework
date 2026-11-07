package tech.kayys.andalus.reasoner;

import java.util.concurrent.CompletionStage;

/** Coordinates strategy resolution, provider execution, and result reporting. */
public interface ReasoningService {

    /**
     * Executes the provider registered for the request strategy.
     *
     * <p>Provider failures are represented as exceptional completion. The service
     * does not silently substitute another strategy or provider.</p>
     */
    CompletionStage<ReasoningResult> reason(ReasoningRequest request);
}
