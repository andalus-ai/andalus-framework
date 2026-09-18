package tech.kayys.wayang.knowledge.snapshot.block;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge answer resolution state merkle dag.
 *
 * <p>Its components capture `root hash`, `nodes`, `node index`.</p>
 *
 * @param rootHash the root hash
 * @param nodes the nodes
 * @param nodeIndex the node index
 */


public record KnowledgeAnswerResolutionStateMerkleDag(
        String rootHash,
        List<KnowledgeAnswerResolutionStateMerkleNode> nodes,
        Map<String, KnowledgeAnswerResolutionStateMerkleNode> nodeIndex
) {
    public KnowledgeAnswerResolutionStateMerkleDag {
        nodes = nodes == null ? List.of() : List.copyOf(nodes);
        nodeIndex = nodeIndex == null ? Map.of() : Map.copyOf(nodeIndex);
    }
}
