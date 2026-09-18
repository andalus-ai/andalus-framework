package tech.kayys.wayang.knowledge.snapshot.pack;

import tech.kayys.wayang.knowledge.integrity.KnowledgeSnapshotIntegrityResult;
import tech.kayys.wayang.knowledge.seal.KnowledgeSnapshotSecureSeal;
import tech.kayys.wayang.knowledge.snapshot.KnowledgeDecisionSnapshot;

/**
 * Defines the contract for knowledge snapshot evidence package service operations in the Wayang framework.
 */


public interface KnowledgeSnapshotEvidencePackageService {

    KnowledgeSnapshotEvidencePackage create(
            KnowledgeDecisionSnapshot snapshot,
            KnowledgeSnapshotIntegrityResult integrity,
            KnowledgeSnapshotSecureSeal seal
    );

    KnowledgeSnapshotPackageVerificationResult verify(
            KnowledgeSnapshotEvidencePackage evidencePackage
    );

    KnowledgeSnapshotPackageVerificationResult verify(
            KnowledgeSnapshotEvidencePackage evidencePackage,
            KnowledgeSnapshotPackageVerificationContext context
    );
}
