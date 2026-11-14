package tech.kayys.andalus.inference.bridge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech.kayys.andalus.harness.inference.InferenceInput;
import tech.kayys.andalus.harness.inference.InferenceMetadata;
import tech.kayys.andalus.harness.inference.InferenceProviderAdapter;
import tech.kayys.andalus.harness.inference.InferenceRequest;
import tech.kayys.andalus.harness.inference.InferenceResult;
import tech.kayys.andalus.harness.inference.InferenceStatus;
import tech.kayys.andalus.harness.inference.InferenceUsage;
import tech.kayys.andalus.inference.CompletionRequest;
import tech.kayys.andalus.inference.CompletionResult;
import tech.kayys.andalus.inference.InferenceProvider;
import tech.kayys.andalus.inference.Message;
import tech.kayys.andalus.inference.MessageRole;
import tech.kayys.andalus.inference.Usage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Bridges an existing Andalus {@link InferenceProvider} implementation to the
 * lightweight {@link InferenceProviderAdapter} SPI.
 *
 * <p>The provider identifier is supplied by composition, rather than inferred
 * from model names or mutable provider metadata. This keeps routing identity
 * explicit when several provider implementations are installed.</p>
 */
public final class AndalusInferenceProviderAdapter implements InferenceProviderAdapter {

    public static final String REASONING_TOKENS_KEY = InferenceMetadata.REASONING_TOKENS;
    public static final String REASONING_BUDGET_KEY = InferenceMetadata.REASONING_TOKEN_BUDGET;

    private final String providerId;
    private final InferenceProvider delegate;
    private final ObjectMapper mapper;

    public AndalusInferenceProviderAdapter(String providerId, InferenceProvider delegate) {
        this(providerId, delegate, new ObjectMapper());
    }

    public AndalusInferenceProviderAdapter(
            String providerId, InferenceProvider delegate, ObjectMapper mapper) {
        if (providerId == null || providerId.isBlank()) {
            throw new IllegalArgumentException("providerId must not be blank");
        }
        this.providerId = providerId;
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    @Override
    public String providerId() {
        return providerId;
    }

    @Override
    public boolean supports(String modelId) {
        if (modelId == null || modelId.isBlank()) {
            return false;
        }
        try {
            Set<String> models = delegate.listModels();
            return models != null && models.contains(modelId);
        } catch (RuntimeException unavailable) {
            return false;
        }
    }

    @Override
    public CompletionStage<InferenceResult> execute(InferenceRequest request) {
        if (request == null || request.plan() == null || request.input() == null) {
            return CompletableFuture.completedFuture(
                    InferenceResult.failed(InferenceStatus.REQUEST_INVALID, "Invalid inference request"));
        }
        if (!supports(request.plan().modelId())) {
            return CompletableFuture.completedFuture(
                    InferenceResult.failed(InferenceStatus.MODEL_UNAVAILABLE,
                            "Provider does not advertise the requested model"));
        }

        final CompletionRequest providerRequest;
        try {
            providerRequest = toProviderRequest(request);
        } catch (RuntimeException invalid) {
            return CompletableFuture.completedFuture(
                    InferenceResult.failed(InferenceStatus.REQUEST_INVALID, "Inference input could not be mapped"));
        }

        try {
            return delegate.generateAsync(providerRequest).handle((completion, failure) -> {
                if (failure != null) {
                    return InferenceResult.failed(InferenceStatus.FAILED,
                            "Provider execution failed: " + failure.getClass().getSimpleName());
                }
                return toInferenceResult(request, completion);
            });
        } catch (RuntimeException failure) {
            return CompletableFuture.completedFuture(
                    InferenceResult.failed(InferenceStatus.FAILED,
                            "Provider invocation failed: " + failure.getClass().getSimpleName()));
        }
    }

    private CompletionRequest toProviderRequest(InferenceRequest request) {
        var plan = request.plan();
        InferenceInput input = request.input();
        Payload payload = payload(input.content());

        long configuredOutputTokens = plan.constraints().maxOutputTokens();
        int maxOutputTokens = configuredOutputTokens <= 0
                ? 2048
                : (int) Math.min(configuredOutputTokens, Integer.MAX_VALUE);

        double temperature = numeric(input.parameters().get("temperature"), 0.7d);
        double topP = numeric(input.parameters().get("top_p"), 1.0d);

        CompletionRequest.CompletionRequestBuilder builder = CompletionRequest.builder()
                .model(plan.modelId())
                .messages(payload.messages())
                .temperature(temperature)
                .topP(topP)
                .maxTokens(maxOutputTokens)
                .parameter("temperature", temperature)
                .parameter("top_p", topP)
                .parameter("max_tokens", maxOutputTokens)
                .metadata("andalus.inference.planId", plan.id())
                .metadata("andalus.inference.providerId", providerId);

        input.parameters().forEach((key, value) -> {
            if (value != null && !Set.of("temperature", "top_p", "max_tokens").contains(key)) {
                builder.parameter(key, value);
            }
        });

        if (plan.matchedCapabilities().contains("structured-json")) {
            builder.responseFormat("json_object");
        }

        // Only forward explicitly namespaced Kallam control metadata. Arbitrary
        // request attributes may contain application data or credentials.
        plan.metadata().forEach((key, value) -> {
            if (key != null && key.startsWith("andalus.") && value != null) {
                builder.metadata(key, value);
            }
        });
        plan.constraints().attributes().forEach((key, value) -> {
            if (key != null && key.startsWith("andalus.") && value != null) {
                builder.metadata(key, value);
            }
        });

        return builder.build();
    }

    private Payload payload(Object content) {
        if (content instanceof Map<?, ?> map) {
            Object system = map.get("systemInstruction");
            String systemInstruction = system instanceof String text ? text : null;
            Map<String, Object> userPayload = new LinkedHashMap<>();
            map.forEach((key, value) -> {
                if (!(key instanceof String name) || !"systemInstruction".equals(name)) {
                    if (key instanceof String name) {
                        userPayload.put(name, value);
                    }
                }
            });
            String userText = serialize(userPayload);
            List<Message> messages = systemInstruction == null || systemInstruction.isBlank()
                    ? List.of(Message.user(userText))
                    : List.of(Message.system(systemInstruction), Message.user(userText));
            return new Payload(messages);
        }
        if (content instanceof String text) {
            return new Payload(List.of(Message.user(text)));
        }
        return new Payload(List.of(Message.user(serialize(content))));
    }

    private String serialize(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException invalid) {
            throw new IllegalArgumentException("Inference payload is not serializable", invalid);
        }
    }

    private InferenceResult toInferenceResult(InferenceRequest request, CompletionResult completion) {
        if (completion == null || completion.getContent() == null || completion.getContent().isBlank()) {
            return InferenceResult.failed(InferenceStatus.INVALID_OUTPUT, "Provider returned empty completion");
        }

        String output = completion.getContent();
        if (request.plan().matchedCapabilities().contains("structured-json") && !isJsonObject(output)) {
            return InferenceResult.failed(InferenceStatus.INVALID_OUTPUT,
                    "Provider did not return a JSON object for a structured-json request");
        }

        Usage providerUsage = completion.usage();
        InferenceUsage usage = providerUsage == null
                ? InferenceUsage.empty()
                : new InferenceUsage(
                        nonNegative(providerUsage.promptTokens()),
                        nonNegative(providerUsage.completionTokens()),
                        nonNegative(providerUsage.totalTokens()),
                        completion.generationTimeMs() > 0
                                ? java.util.Optional.of(completion.generationTimeMs())
                                : java.util.Optional.empty(),
                        java.util.Optional.empty());

        Map<String, Object> metadata = new LinkedHashMap<>();
        Object reasoningTokens = completion.metadata() == null
                ? null
                : completion.metadata().get(REASONING_TOKENS_KEY);
        if (reasoningTokens != null) {
            metadata.put(REASONING_TOKENS_KEY, reasoningTokens);
        }

        String modelId = completion.model() == null || completion.model().isBlank()
                ? request.plan().modelId()
                : completion.model();

        return new InferenceResult(
                InferenceStatus.COMPLETED,
                output,
                usage,
                java.util.Optional.of(modelId),
                java.util.Optional.of(providerId),
                metadata);
    }

    private boolean isJsonObject(String output) {
        try {
            JsonNode node = mapper.readTree(output);
            return node != null && node.isObject();
        } catch (JsonProcessingException invalid) {
            return false;
        }
    }

    private static long nonNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Provider returned negative token usage");
        }
        return value;
    }

    private static double numeric(Object value, double fallback) {
        return value instanceof Number number ? number.doubleValue() : fallback;
    }

    private record Payload(List<Message> messages) {}
}
