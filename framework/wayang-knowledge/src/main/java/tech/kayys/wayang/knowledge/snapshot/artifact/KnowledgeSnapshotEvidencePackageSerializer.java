package tech.kayys.wayang.knowledge.snapshot.artifact;

import tech.kayys.wayang.knowledge.snapshot.pack.KnowledgeSnapshotEvidencePackage;

/**
 * Defines the contract for knowledge snapshot evidence package serializer operations in the Wayang framework.
 */


public interface KnowledgeSnapshotEvidencePackageSerializer {

    byte[] serialize(KnowledgeSnapshotEvidencePackage evidencePackage);

    KnowledgeSnapshotEvidencePackage deserialize(byte[] content);
}
