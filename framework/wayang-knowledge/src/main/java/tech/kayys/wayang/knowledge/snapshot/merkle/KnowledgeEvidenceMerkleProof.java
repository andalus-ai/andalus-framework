package tech.kayys.wayang.knowledge.snapshot.merkle;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence merkle proof.
 *
 * <p>Its components capture `leaf id`, `leaf hash`, `root hash`, `steps`, `metadata`.</p>
 *
 * @param leafId the leaf id
 * @param leafHash the leaf hash
 * @param rootHash the root hash
 * @param steps the steps
 * @param metadata the metadata
 */


public record KnowledgeEvidenceMerkleProof(
        String leafId,
        String leafHash,
        String rootHash,
        List<KnowledgeEvidenceMerkleProofStep> steps,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceMerkleProof {
        steps = steps == null ? List.of() : List.copyOf(steps);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
