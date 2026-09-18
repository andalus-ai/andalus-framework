package tech.kayys.wayang.knowledge.snapshot.merkle;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence merkle tree.
 *
 * <p>Its components capture `algorithm`, `root hash`, `leaves`, `nodes`, `metadata`.</p>
 *
 * @param algorithm the algorithm
 * @param rootHash the root hash
 * @param leaves the leaves
 * @param nodes the nodes
 * @param metadata the metadata
 */


public record KnowledgeEvidenceMerkleTree(
        String algorithm,
        String rootHash,
        List<KnowledgeEvidenceMerkleLeaf> leaves,
        Map<String, KnowledgeEvidenceMerkleNode> nodes,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceMerkleTree {
        leaves = leaves == null ? List.of() : List.copyOf(leaves);
        nodes = nodes == null ? Map.of() : Map.copyOf(nodes);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
