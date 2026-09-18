package tech.kayys.wayang.knowledge.snapshot.pack;

/**
 * Defines the contract for knowledge snapshot evidence package verifier operations in the Wayang framework.
 */


public interface KnowledgeSnapshotEvidencePackageVerifier {

    KnowledgeSnapshotPackageVerificationResult verify(
            KnowledgeSnapshotEvidencePackage evidencePackage
    );

    default KnowledgeSnapshotPackageVerificationResult verify(
            KnowledgeSnapshotEvidencePackage evidencePackage,
            KnowledgeSnapshotPackageVerificationContext context
    ) {
        return verify(evidencePackage);
    }
}
