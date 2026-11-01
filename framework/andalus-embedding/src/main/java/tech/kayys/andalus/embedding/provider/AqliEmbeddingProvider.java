package tech.kayys.andalus.embedding.provider;

import tech.kayys.aqli.sdk.core.AqliSdk;
import tech.kayys.aqli.spi.embedding.EmbeddingRequest;
import tech.kayys.andalus.embedding.EmbeddingProvider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.inject.Instance;

/**
 * High-performance embedding provider using local Aqli (Llama.cpp/GGUF).
 */
@ApplicationScoped
public class AqliEmbeddingProvider implements EmbeddingProvider {

    private final Instance<AqliSdk> sdkInstance;

    @Inject
    public AqliEmbeddingProvider(Instance<AqliSdk> sdkInstance) {
        this.sdkInstance = sdkInstance;
    }

    @Override
    public String name() {
        return "aqli";
    }

    @Override
    public boolean supports(String model) {
        // Broadly support local GGUF models via Aqli
        return model != null && !model.isBlank();
    }

    @Override
    public List<float[]> embedAll(List<String> inputs, String model) {
        if (inputs == null || inputs.isEmpty()) {
            return Collections.emptyList();
        }

        EmbeddingRequest request = new EmbeddingRequest(
                UUID.randomUUID().toString(),
                model,
                inputs,
                Collections.emptyMap());

        if (!sdkInstance.isResolvable()) {
            throw new tech.kayys.andalus.embedding.EmbeddingException("AqliSdk is not available in the current environment.");
        }

        try {
            tech.kayys.aqli.spi.embedding.EmbeddingResponse response = sdkInstance.get().createEmbedding(request);
            return response.embeddings();
        } catch (tech.kayys.aqli.sdk.exception.SdkException e) {
            throw new tech.kayys.andalus.embedding.EmbeddingException("Aqli embedding failed: " + e.getMessage());
        }
    }
}
