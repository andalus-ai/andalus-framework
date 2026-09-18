package tech.kayys.wayang.harness.model;

import java.util.Set;

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
