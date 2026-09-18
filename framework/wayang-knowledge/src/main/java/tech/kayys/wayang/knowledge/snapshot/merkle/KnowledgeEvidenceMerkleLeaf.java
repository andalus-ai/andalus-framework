package tech.kayys.wayang.knowledge.snapshot.merkle;

import java.util.Map;

/**
 * Represents a knowledge evidence merkle leaf.
 *
 * <p>Its components capture `leaf id`, `artifact id`, `resource type`, `content hash`, `size`, and other values.</p>
 *
 * @param leafId the leaf id
 * @param artifactId the artifact id
 * @param resourceType the resource type
 * @param contentHash the content hash
 * @param size the size
 * @param metadata the metadata
 */


public record KnowledgeEvidenceMerkleLeaf(
        String leafId,
        String artifactId,
        String resourceType,
        String contentHash,
        long size,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceMerkleLeaf {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
