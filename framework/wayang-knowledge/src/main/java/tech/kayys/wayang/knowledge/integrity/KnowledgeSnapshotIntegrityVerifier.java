package tech.kayys.wayang.knowledge.integrity;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeDecisionSnapshot;

/**
 * Defines the contract for knowledge snapshot integrity verifier operations in the Wayang framework.
 */


public interface KnowledgeSnapshotIntegrityVerifier {

    KnowledgeSnapshotIntegrityResult verify(KnowledgeDecisionSnapshot snapshot);
}
