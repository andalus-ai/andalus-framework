package tech.kayys.wayang.harness.context;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a default assembled context.
 *
 * <p>Its components capture `id`, `items`, `metadata`, `estimated tokens`.</p>
 *
 * @param id the id
 * @param items the items
 * @param metadata the metadata
 * @param estimatedTokens the estimated tokens
 */


public record DefaultAssembledContext(
        ContextId id,
        List<ContextItem> items,
        Map<String, Object> metadata,
        long estimatedTokens
) implements AssembledContext {

    public DefaultAssembledContext {
        id = id == null ? ContextId.generate() : id;
        items = items == null ? List.of() : List.copyOf(items);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
