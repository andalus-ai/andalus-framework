package tech.kayys.andalus.knowledge.snapshot.lifecycle;

import tech.kayys.andalus.knowledge.snapshot.KnowledgeDecisionSnapshot;
import tech.kayys.andalus.knowledge.snapshot.KnowledgeSnapshotId;

/**
 * Defines the contract for knowledge snapshot archive operations in the Wayang framework.
 */


public interface KnowledgeSnapshotArchive {

    void archive(KnowledgeDecisionSnapshot snapshot);

    boolean contains(KnowledgeSnapshotId snapshotId);

    void restore(KnowledgeSnapshotId snapshotId);
}
