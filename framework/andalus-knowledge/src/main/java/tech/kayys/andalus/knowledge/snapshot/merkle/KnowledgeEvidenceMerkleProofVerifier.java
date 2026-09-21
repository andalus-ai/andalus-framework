package tech.kayys.andalus.knowledge.snapshot.merkle;

/**
 * Defines the contract for knowledge evidence merkle proof verifier operations in the Wayang framework.
 */


public interface KnowledgeEvidenceMerkleProofVerifier {
    boolean verify(KnowledgeEvidenceMerkleProof proof);
}
