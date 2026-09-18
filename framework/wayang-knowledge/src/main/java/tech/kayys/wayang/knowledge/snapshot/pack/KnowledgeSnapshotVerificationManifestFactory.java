package tech.kayys.wayang.knowledge.snapshot.pack;

import tech.kayys.wayang.knowledge.integrity.KnowledgeSnapshotIntegrityResult;
import tech.kayys.wayang.knowledge.seal.KnowledgeSnapshotSecureSeal;
import tech.kayys.wayang.knowledge.snapshot.KnowledgeDecisionSnapshot;

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
