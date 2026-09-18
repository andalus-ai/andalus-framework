package tech.kayys.wayang.knowledge.snapshot.lifecycle;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.time.Instant;

/**
 * Defines the contract for knowledge snapshot lifecycle service operations in the Wayang framework.
 */


public interface KnowledgeSnapshotLifecycleService {

    KnowledgeSnapshotLifecycleDecision evaluate(KnowledgeSnapshotId snapshotId, Instant now);

    void retain(KnowledgeSnapshotId snapshotId, KnowledgeSnapshotRetentionClass retentionClass, String reason);

    void release(KnowledgeSnapshotId snapshotId);

    void addHold(KnowledgeSnapshotHold hold);

    void removeHold(String holdId);
}
