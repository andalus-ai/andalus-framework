package tech.kayys.wayang.state.context;

import java.util.Map;
import java.util.Objects;

/**
 * Reference to a context fragment (memory, evidence, tool result, etc.)
 */
public record ContextFragmentRef(
        String fragmentId,
        String fragmentType,
        long tokenCount,
        Map<String, Object> metadata
) {
    public ContextFragmentRef {
        Objects.requireNonNull(fragmentId, "fragmentId cannot be null");
        Objects.requireNonNull(fragmentType, "fragmentType cannot be null");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
