package tech.kayys.andalus.knowledge.snapshot.pack;

import tech.kayys.andalus.knowledge.integrity.KnowledgeSnapshotIntegrityResult;
import tech.kayys.andalus.knowledge.seal.KnowledgeSnapshotSecureSeal;
import tech.kayys.andalus.knowledge.snapshot.KnowledgeDecisionSnapshot;

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
