package tech.kayys.andalus.knowledge.snapshot.pack;

/**
 * Defines the contract for knowledge snapshot evidence package codec operations in the Wayang framework.
 */


public interface KnowledgeSnapshotEvidencePackageCodec {
    byte[] encode(KnowledgeSnapshotEvidencePackage evidencePackage);
    KnowledgeSnapshotEvidencePackage decode(byte[] data);
}
