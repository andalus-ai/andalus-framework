package tech.kayys.wayang.knowledge.snapshot.merkle;

/**
 * Defines the contract for knowledge evidence partial verification service operations in the Wayang framework.
 */


public interface KnowledgeEvidencePartialVerificationService {
    KnowledgeEvidencePartialVerificationResult verify(KnowledgeEvidenceMerkleProof proof);
}
