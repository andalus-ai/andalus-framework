package tech.kayys.andalus.knowledge.snapshot.dependency;

import tech.kayys.andalus.knowledge.snapshot.KnowledgeSnapshotId;

import java.util.List;

/**
 * Defines the contract for knowledge snapshot protection service operations in the Wayang framework.
 */


public interface KnowledgeSnapshotProtectionService {

    boolean isProtected(KnowledgeSnapshotId snapshotId);

    List<KnowledgeSnapshotDependency> protectionReasons(KnowledgeSnapshotId snapshotId);
}
