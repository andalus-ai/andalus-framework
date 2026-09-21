package tech.kayys.andalus.knowledge.seal;

/**
 * Defines the contract for knowledge snapshot seal verification service operations in the Andalus framework.
 */


public interface KnowledgeSnapshotSealVerificationService {

    KnowledgeSnapshotSealVerificationResult verify(
            KnowledgeSnapshotSecureSeal seal,
            KnowledgeSnapshotSealPayload payload
    );
}
