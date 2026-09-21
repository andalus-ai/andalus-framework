package tech.kayys.andalus.knowledge.snapshot.lifecycle;

import tech.kayys.andalus.knowledge.snapshot.KnowledgeSnapshotId;

/**
 * Defines the contract for knowledge snapshot deletion service operations in the Wayang framework.
 */


public interface KnowledgeSnapshotDeletionService {

    void delete(KnowledgeSnapshotId snapshotId);
}
