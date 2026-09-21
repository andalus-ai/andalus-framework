package tech.kayys.andalus.knowledge.integrity;

import tech.kayys.andalus.knowledge.snapshot.KnowledgeDecisionSnapshot;

/**
 * Defines the contract for knowledge snapshot integrity verifier operations in the Andalus framework.
 */


public interface KnowledgeSnapshotIntegrityVerifier {

    KnowledgeSnapshotIntegrityResult verify(KnowledgeDecisionSnapshot snapshot);
}
