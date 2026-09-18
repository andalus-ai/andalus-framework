package tech.kayys.wayang.knowledge;

import java.util.Map;

/**
 * Represents a knowledge source descriptor.
 *
 * <p>Its components capture `id`, `name`, `type`, `read only`, `metadata`.</p>
 *
 * @param id the id
 * @param name the name
 * @param type the type
 * @param readOnly the read only
 * @param metadata the metadata
 */


public record KnowledgeSourceDescriptor(
        String id,
        String name,
        String type,
        boolean readOnly,
        Map<String, Object> metadata
) {

    public KnowledgeSourceDescriptor(String id, String name, String type, boolean readOnly) {
        this(id, name, type, readOnly, Map.of());
    }

    public KnowledgeSourceDescriptor {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
