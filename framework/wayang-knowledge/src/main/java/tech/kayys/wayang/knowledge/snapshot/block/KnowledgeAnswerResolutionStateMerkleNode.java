package tech.kayys.wayang.knowledge.snapshot.block;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge answer resolution state merkle node.
 *
 * <p>Its components capture `node id`, `hash`, `children`, `leaf`, `metadata`.</p>
 *
 * @param nodeId the node id
 * @param hash the hash
 * @param children the children
 * @param leaf the leaf
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionStateMerkleNode(
        String nodeId,
        String hash,
        List<String> children,
        boolean leaf,
        Map<String, String> metadata
) {
    public KnowledgeAnswerResolutionStateMerkleNode {
        children = children == null ? List.of() : List.copyOf(children);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
