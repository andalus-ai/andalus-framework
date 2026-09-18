package tech.kayys.wayang.knowledge.seal;

/**
 * Defines the contract for knowledge snapshot seal verification service operations in the Wayang framework.
 */


public interface KnowledgeSnapshotSealVerificationService {

    KnowledgeSnapshotSealVerificationResult verify(
            KnowledgeSnapshotSecureSeal seal,
            KnowledgeSnapshotSealPayload payload
    );
}
