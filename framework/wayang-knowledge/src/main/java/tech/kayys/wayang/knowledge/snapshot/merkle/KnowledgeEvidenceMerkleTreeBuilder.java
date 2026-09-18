package tech.kayys.wayang.knowledge.snapshot.merkle;

import java.util.List;

/**
 * Defines the contract for knowledge evidence merkle tree builder operations in the Wayang framework.
 */


public interface KnowledgeEvidenceMerkleTreeBuilder {
    KnowledgeEvidenceMerkleTree build(List<KnowledgeEvidenceMerkleLeaf> leaves);
}
