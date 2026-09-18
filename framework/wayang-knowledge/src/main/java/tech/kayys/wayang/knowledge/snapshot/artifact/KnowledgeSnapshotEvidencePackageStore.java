package tech.kayys.wayang.knowledge.snapshot.artifact;

import tech.kayys.wayang.knowledge.snapshot.pack.KnowledgeSnapshotEvidencePackage;

import java.util.Optional;

/**
 * Defines the contract for knowledge snapshot evidence package store operations in the Wayang framework.
 */


public interface KnowledgeSnapshotEvidencePackageStore {

    KnowledgeSnapshotEvidencePackageArtifact put(
            KnowledgeSnapshotEvidencePackage evidencePackage
    );

    Optional<KnowledgeSnapshotEvidencePackage> get(
            KnowledgeEvidenceArtifactId artifactId
    );

    boolean exists(
            KnowledgeEvidenceArtifactId artifactId
    );
}
