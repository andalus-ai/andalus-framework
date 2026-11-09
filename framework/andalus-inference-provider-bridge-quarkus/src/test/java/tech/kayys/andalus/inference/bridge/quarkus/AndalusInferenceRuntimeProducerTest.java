package tech.kayys.andalus.inference.bridge.quarkus;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.inference.InferenceInput;
import tech.kayys.andalus.harness.inference.InferencePlan;
import tech.kayys.andalus.harness.inference.InferenceRequest;
import tech.kayys.andalus.harness.inference.InferenceStatus;
import tech.kayys.andalus.inference.Choice;
import tech.kayys.andalus.inference.CompletionResult;
import tech.kayys.andalus.inference.InferenceProvider;
import tech.kayys.andalus.inference.Message;
import tech.kayys.andalus.inference.ModelInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AndalusInferenceRuntimeProducerTest {

    @Test
    void composesCdiProvidersUsingTheirAdvertisedProviderIdentity() {
        InferenceProvider provider = provider("openai", "gpt-test");
        when(provider.generateAsync(any())).thenReturn(CompletableFuture.completedFuture(
                new CompletionResult("id", "gpt-test",
                        List.of(Choice.of(Message.assistant("{\"ok\":true}"))),
                        null, Map.of(), 1)));

        var runtime = AndalusInferenceRuntimeProducer.createRuntime(List.of(provider));
        var request = new InferenceRequest(
                InferencePlan.direct("gpt-test", "openai", Set.of("text-generation")),
                InferenceInput.of("hello"));

        var result = runtime.execute(request).toCompletableFuture().join();

        assertEquals(InferenceStatus.COMPLETED, result.status());
        assertEquals("openai", result.providerId().orElseThrow());
        assertEquals("gpt-test", result.modelId().orElseThrow());
    }

    @Test
    void failsFastWhenProviderDoesNotDeclareStableIdentity() {
        InferenceProvider provider = provider(null, "model-x");
        assertThrows(IllegalStateException.class,
                () -> AndalusInferenceRuntimeProducer.createRuntime(List.of(provider)));
    }

    @Test
    void rejectsDuplicateProviderIdsInsteadOfSilentlyReplacingAProvider() {
        InferenceProvider first = provider("openai", "gpt-one");
        InferenceProvider second = provider("openai", "gpt-two");

        assertThrows(IllegalStateException.class,
                () -> AndalusInferenceRuntimeProducer.createRuntime(List.of(first, second)));
    }

    private static InferenceProvider provider(String providerId, String modelId) {
        InferenceProvider provider = mock(InferenceProvider.class);
        when(provider.listModels()).thenReturn(Set.of(modelId));
        when(provider.getModelInfo(modelId)).thenReturn(
                new ModelInfo(modelId, modelId, providerId, Set.of("chat"), Map.of()));
        return provider;
    }
}
