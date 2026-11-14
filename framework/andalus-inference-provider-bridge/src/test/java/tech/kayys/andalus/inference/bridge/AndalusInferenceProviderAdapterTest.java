package tech.kayys.andalus.inference.bridge;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.inference.InferenceConstraints;
import tech.kayys.andalus.harness.inference.InferenceInput;
import tech.kayys.andalus.harness.inference.InferencePlan;
import tech.kayys.andalus.harness.inference.InferenceRequest;
import tech.kayys.andalus.harness.inference.InferenceStatus;
import tech.kayys.andalus.inference.Choice;
import tech.kayys.andalus.inference.CompletionRequest;
import tech.kayys.andalus.inference.CompletionResult;
import tech.kayys.andalus.inference.InferenceProvider;
import tech.kayys.andalus.inference.Message;
import tech.kayys.andalus.inference.Usage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AndalusInferenceProviderAdapterTest {

    private static final String JSON = """
            {"schemaVersion":"test-v1","status":"COMPLETE"}
            """;

    @Test
    void mapsStructuredRequestAndProviderUsageIntoRuntimeContract() {
        InferenceProvider provider = mock(InferenceProvider.class);
        when(provider.listModels()).thenReturn(Set.of("research-model"));
        when(provider.generateAsync(any())).thenReturn(CompletableFuture.completedFuture(
                completion("research-model", JSON,
                        Map.of(AndalusInferenceProviderAdapter.REASONING_TOKENS_KEY, 37))));

        AndalusInferenceProviderAdapter adapter =
                new AndalusInferenceProviderAdapter("openai", provider);

        var result = adapter.execute(request()).toCompletableFuture().join();

        assertEquals(InferenceStatus.COMPLETED, result.status());
        assertEquals("openai", result.providerId().orElseThrow());
        assertEquals("research-model", result.modelId().orElseThrow());
        assertEquals(JSON.trim(), result.output().toString().trim());
        assertEquals(37, result.metadata().get(AndalusInferenceProviderAdapter.REASONING_TOKENS_KEY));
        assertEquals(120, result.usage().inputTokens());
        assertEquals(80, result.usage().outputTokens());
        assertEquals(205, result.usage().totalTokens());

        var captor = org.mockito.ArgumentCaptor.forClass(CompletionRequest.class);
        verify(provider).generateAsync(captor.capture());
        CompletionRequest sent = captor.getValue();
        assertEquals("research-model", sent.model());
        assertEquals("json_object", sent.responseFormat());
        assertEquals(4096, sent.maxTokens());
        assertEquals(2, sent.messages().size());
        assertEquals("You are a scholarly research assistant.", sent.messages().get(0).content());
        assertTrue(sent.messages().get(1).content().contains("\"methodology\""));
        assertTrue(sent.messages().get(1).content().contains("\"shafii\""));
        assertEquals(75, sent.metadata().get(AndalusInferenceProviderAdapter.REASONING_BUDGET_KEY));
        assertFalse(sent.metadata().containsKey("private.application.attribute"));
    }

    @Test
    void rejectsNonJsonOutputForStructuredRequest() {
        InferenceProvider provider = mock(InferenceProvider.class);
        when(provider.listModels()).thenReturn(Set.of("research-model"));
        when(provider.generateAsync(any())).thenReturn(CompletableFuture.completedFuture(
                completion("research-model", "Here is the result", Map.of())));

        var result = new AndalusInferenceProviderAdapter("openai", provider)
                .execute(request()).toCompletableFuture().join();

        assertEquals(InferenceStatus.INVALID_OUTPUT, result.status());
        assertTrue(result.metadata().get("error").toString().contains("JSON object"));
    }

    @Test
    void rejectsModelsNotAdvertisedByDelegate() {
        InferenceProvider provider = mock(InferenceProvider.class);
        when(provider.listModels()).thenReturn(Set.of("other-model"));

        var result = new AndalusInferenceProviderAdapter("openai", provider)
                .execute(request()).toCompletableFuture().join();

        assertEquals(InferenceStatus.MODEL_UNAVAILABLE, result.status());
        verify(provider, never()).generateAsync(any());
    }

    @Test
    void rejectsMalformedRequestsWithoutInvokingProvider() {
        InferenceProvider provider = mock(InferenceProvider.class);
        var adapter = new AndalusInferenceProviderAdapter("openai", provider);

        var result = adapter.execute(null).toCompletableFuture().join();

        assertEquals(InferenceStatus.REQUEST_INVALID, result.status());
        verifyNoInteractions(provider);
    }

    private static InferenceRequest request() {
        Map<String, Object> payload = Map.of(
                "systemInstruction", "You are a scholarly research assistant.",
                "methodology", Map.of("id", "shafii", "version", "1"),
                "task", "Analyze the supplied evidence.",
                "evidence", List.of(Map.of("id", "evidence-1", "text", "supplied source text")));
        InferenceConstraints constraints = new InferenceConstraints(
                8192, 4096, 12288, 45000, Optional.empty(),
                Map.of(AndalusInferenceProviderAdapter.REASONING_BUDGET_KEY, 75));
        InferencePlan plan = new InferencePlan(
                "plan-1", "kallam.analysis", "research-model", Optional.of("openai"),
                Set.of("text-generation", "structured-json"), constraints, "DIRECT",
                Map.of("kallam.schema.version", "test-v1", "private.application.attribute", "do-not-forward"));
        return new InferenceRequest(plan,
                new InferenceInput(payload, Map.of("temperature", 0.2), Map.of("private", "do-not-forward")),
                Map.of("private.application.attribute", "do-not-forward"));
    }

    private static CompletionResult completion(String model, String content, Map<String, Object> metadata) {
        return new CompletionResult("completion-1", model,
                List.of(Choice.of(Message.assistant(content))), new Usage(120, 80, 205, 0.0), metadata, 321);
    }
}
