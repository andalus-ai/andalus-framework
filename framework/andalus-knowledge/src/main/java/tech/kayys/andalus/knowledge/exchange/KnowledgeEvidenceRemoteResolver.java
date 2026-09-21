package tech.kayys.andalus.knowledge.exchange;

import tech.kayys.andalus.knowledge.snapshot.artifact.KnowledgeEvidenceArtifact;
import tech.kayys.andalus.knowledge.snapshot.artifact.KnowledgeEvidenceArtifactId;
import tech.kayys.andalus.knowledge.snapshot.merkle.KnowledgeEvidenceMerkleProof;
import tech.kayys.andalus.knowledge.snapshot.pack.KnowledgeSnapshotVerificationManifest;

import java.util.Optional;

/**
 * Defines the contract for knowledge evidence remote resolver operations in the Wayang framework.
 */


public interface KnowledgeEvidenceRemoteResolver {

    Optional<KnowledgeEvidenceArtifact> resolve(
            KnowledgeEvidenceArtifactId artifactId
    );

    Optional<KnowledgeSnapshotVerificationManifest> manifest(
            KnowledgeEvidenceArtifactId artifactId
    );

    Optional<KnowledgeEvidenceMerkleProof> proof(
            KnowledgeEvidenceArtifactId artifactId,
            String leafId
    );
}
