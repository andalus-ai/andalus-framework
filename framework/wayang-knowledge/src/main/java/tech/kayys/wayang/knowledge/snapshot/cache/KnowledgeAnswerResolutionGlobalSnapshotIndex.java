package tech.kayys.wayang.knowledge.snapshot.cache;

import java.util.List;

/**
 * Defines the contract for knowledge answer resolution global snapshot index operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionGlobalSnapshotIndex {

    void index(KnowledgeAnswerResolutionSnapshotBlockReference reference);

    List<KnowledgeAnswerResolutionSnapshotBlockReference> findBySnapshot(String snapshotId);

    List<KnowledgeAnswerResolutionSnapshotBlockReference> findByBlock(KnowledgeAnswerResolutionStateBlockId blockId);

    void removeSnapshot(String snapshotId);
}
