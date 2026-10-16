package tech.kayys.andalus.harness.reflection;

import java.util.concurrent.CompletionStage;

/**
 * Primary SPI for conducting structured reflection on execution outcomes, failures, and feedback.
 */
public interface ReflectionRuntime {

    /**
     * Executes reflection for the given request and produces analytical findings and improvement proposals.
     *
     * @param request reflection request detailing trigger and observation references
     * @return completion stage resolving to reflection result
     */
    CompletionStage<ReflectionResult> reflect(ReflectionRequest request);
}
