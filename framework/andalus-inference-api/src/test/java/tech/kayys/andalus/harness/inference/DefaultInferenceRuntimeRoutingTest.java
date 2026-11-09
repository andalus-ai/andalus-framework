package tech.kayys.andalus.harness.inference;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class DefaultInferenceRuntimeRoutingTest {

    @Test
    void requiresExplicitProviderWhenSeveralProvidersSupportTheSameModel() {
        var runtime = new DefaultInferenceRuntime(Set.of(
                provider("openai", "shared-model"),
                provider("ollama", "shared-model")));

        var result = runtime.execute(InferenceRequest.of(
                InferencePlan.direct("shared-model", Set.of()), "hello"))
                .toCompletableFuture().join();

        assertEquals(InferenceStatus.PROVIDER_UNAVAILABLE, result.status());
        assertTrue(result.metadata().get("error").toString().contains("specify an explicit provider ID"));
    }

    @Test
    void routesToExplicitProviderWhenSeveralProvidersSupportTheSameModel() {
        var runtime = new DefaultInferenceRuntime(Set.of(
                provider("openai", "shared-model"),
                provider("ollama", "shared-model")));

        var result = runtime.execute(InferenceRequest.of(
                InferencePlan.direct("shared-model", "ollama", Set.of()), "hello"))
                .toCompletableFuture().join();

        assertEquals(InferenceStatus.COMPLETED, result.status());
        assertEquals("ollama", result.providerId().orElseThrow());
    }

    @Test
    void explicitProviderMustAdvertiseTheRequestedModel() {
        var runtime = new DefaultInferenceRuntime(Set.of(provider("openai", "gpt-model")));

        var result = runtime.execute(InferenceRequest.of(
                InferencePlan.direct("other-model", "openai", Set.of()), "hello"))
                .toCompletableFuture().join();

        assertEquals(InferenceStatus.PROVIDER_UNAVAILABLE, result.status());
        assertTrue(result.metadata().get("error").toString().contains("does not support model"));
    }

    @Test
    void rejectsDuplicateProviderRegistration() {
        var runtime = new DefaultInferenceRuntime();
        runtime.registerProvider(provider("openai", "gpt-model"));

        assertThrows(IllegalArgumentException.class,
                () -> runtime.registerProvider(provider("openai", "other-model")));
    }


    @Test
    void normalizesSynchronousProviderInvocationFailures() {
        InferenceProviderAdapter broken = new InferenceProviderAdapter() {
            @Override
            public String providerId() {
                return "broken";
            }

            @Override
            public boolean supports(String modelId) {
                return true;
            }

            @Override
            public java.util.concurrent.CompletionStage<InferenceResult> execute(InferenceRequest request) {
                throw new IllegalStateException("transport initialization failed");
            }
        };
        var runtime = new DefaultInferenceRuntime(Set.of(broken));

        var result = runtime.execute(InferenceRequest.of(
                InferencePlan.direct("model-x", "broken", Set.of()), "hello"))
                .toCompletableFuture().join();

        assertEquals(InferenceStatus.FAILED, result.status());
        assertTrue(result.metadata().get("error").toString().contains("could not be started"));
    }

    private static InferenceProviderAdapter provider(String id, String model) {
        return new InferenceProviderAdapter() {
            @Override
            public String providerId() {
                return id;
            }

            @Override
            public boolean supports(String modelId) {
                return model.equals(modelId);
            }

            @Override
            public CompletableFuture<InferenceResult> execute(InferenceRequest request) {
                return CompletableFuture.completedFuture(InferenceResult.completed(
                        model, id, "ok", InferenceUsage.empty()));
            }
        };
    }
}
