package tech.kayys.wayang.knowledge.integrity;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

/**
 * Defines the contract for knowledge snapshot integrity service operations in the Wayang framework.
 */


public interface KnowledgeSnapshotIntegrityService {

    KnowledgeSnapshotIntegrityResult verify(KnowledgeSnapshotId snapshotId);
}
