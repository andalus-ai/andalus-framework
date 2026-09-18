package tech.kayys.wayang.knowledge.snapshot.lifecycle;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeDecisionSnapshot;
import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

/**
 * Defines the contract for knowledge snapshot archive operations in the Wayang framework.
 */


public interface KnowledgeSnapshotArchive {

    void archive(KnowledgeDecisionSnapshot snapshot);

    boolean contains(KnowledgeSnapshotId snapshotId);

    void restore(KnowledgeSnapshotId snapshotId);
}
