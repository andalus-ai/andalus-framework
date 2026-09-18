package tech.kayys.wayang.harness.model;

import java.util.Set;

/**
 * Represents a model capabilities.
 *
 * <p>Its components capture `streaming`, `tool calling`, `structured output`, `vision`, `audio`, and other values.</p>
 *
 * @param streaming the streaming
 * @param toolCalling the tool calling
 * @param structuredOutput the structured output
 * @param vision the vision
 * @param audio the audio
 * @param reasoning the reasoning
 * @param embeddings the embeddings
 * @param customCapabilities the custom capabilities
 */


public record ModelCapabilities(
        boolean streaming,
        boolean toolCalling,
        boolean structuredOutput,
        boolean vision,
        boolean audio,
        boolean reasoning,
        boolean embeddings,
        Set<String> customCapabilities
) {
    public ModelCapabilities {
        customCapabilities = customCapabilities != null ? Set.copyOf(customCapabilities) : Set.of();
    }

    public static ModelCapabilities standardChat() {
        return new ModelCapabilities(true, true, true, false, false, true, false, Set.of());
    }

    public static ModelCapabilities embeddingsOnly() {
        return new ModelCapabilities(false, false, false, false, false, false, true, Set.of());
    }
}
