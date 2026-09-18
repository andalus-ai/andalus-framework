package tech.kayys.wayang.knowledge.snapshot.merkle;

/**
 * Defines the contract for knowledge evidence merkle proof builder operations in the Wayang framework.
 */


public interface KnowledgeEvidenceMerkleProofBuilder {
    KnowledgeEvidenceMerkleProof build(KnowledgeEvidenceMerkleTree tree, String leafId);
}
