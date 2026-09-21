package tech.kayys.andalus.knowledge.seal;

import tech.kayys.andalus.knowledge.snapshot.KnowledgeSnapshotId;

import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for knowledge trust anchor store operations in the Andalus framework.
 */


public interface KnowledgeTrustAnchorStore {

    void save(KnowledgeSnapshotExternalTrustAnchor anchor);

    Optional<KnowledgeSnapshotExternalTrustAnchor> get(String anchorId);

    List<KnowledgeSnapshotExternalTrustAnchor> findBySnapshot(KnowledgeSnapshotId snapshotId);
}
