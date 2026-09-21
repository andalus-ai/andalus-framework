package tech.kayys.andalus.knowledge.snapshot.pack;

import tech.kayys.andalus.knowledge.integrity.KnowledgeSnapshotIntegrityResult;
import tech.kayys.andalus.knowledge.seal.KnowledgeSnapshotSecureSeal;
import tech.kayys.andalus.knowledge.snapshot.KnowledgeDecisionSnapshot;

/**
 * Defines the contract for knowledge snapshot verification manifest factory operations in the Wayang framework.
 */


public interface KnowledgeSnapshotVerificationManifestFactory {
    KnowledgeSnapshotVerificationManifest create(
            KnowledgeDecisionSnapshot snapshot,
            KnowledgeSnapshotIntegrityResult integrity,
            KnowledgeSnapshotSecureSeal seal
    );
}
