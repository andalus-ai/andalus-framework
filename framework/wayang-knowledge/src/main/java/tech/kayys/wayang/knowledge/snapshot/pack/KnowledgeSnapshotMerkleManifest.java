package tech.kayys.wayang.knowledge.snapshot.pack;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge snapshot merkle manifest.
 *
 * <p>Its components capture `algorithm`, `root hash`, `leaf count`, `required leaf ids`, `metadata`.</p>
 *
 * @param algorithm the algorithm
 * @param rootHash the root hash
 * @param leafCount the leaf count
 * @param requiredLeafIds the required leaf ids
 * @param metadata the metadata
 */


public record KnowledgeSnapshotMerkleManifest(
        String algorithm,
        String rootHash,
        int leafCount,
        List<String> requiredLeafIds,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotMerkleManifest {
        requiredLeafIds = requiredLeafIds == null ? List.of() : List.copyOf(requiredLeafIds);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
