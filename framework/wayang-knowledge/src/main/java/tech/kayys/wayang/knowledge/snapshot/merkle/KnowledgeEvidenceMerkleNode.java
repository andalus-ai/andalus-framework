package tech.kayys.wayang.knowledge.snapshot.merkle;

import java.util.Map;

/**
 * Represents a knowledge evidence merkle node.
 *
 * <p>Its components capture `hash`, `left hash`, `right hash`, `leaf`, `artifact id`, and other values.</p>
 *
 * @param hash the hash
 * @param leftHash the left hash
 * @param rightHash the right hash
 * @param leaf the leaf
 * @param artifactId the artifact id
 * @param metadata the metadata
 */


public record KnowledgeEvidenceMerkleNode(
        String hash,
        String leftHash,
        String rightHash,
        boolean leaf,
        String artifactId,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceMerkleNode {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
