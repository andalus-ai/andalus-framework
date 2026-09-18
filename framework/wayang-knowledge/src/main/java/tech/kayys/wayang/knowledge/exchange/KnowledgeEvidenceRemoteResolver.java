package tech.kayys.wayang.knowledge.exchange;

import tech.kayys.wayang.knowledge.snapshot.artifact.KnowledgeEvidenceArtifact;
import tech.kayys.wayang.knowledge.snapshot.artifact.KnowledgeEvidenceArtifactId;
import tech.kayys.wayang.knowledge.snapshot.merkle.KnowledgeEvidenceMerkleProof;
import tech.kayys.wayang.knowledge.snapshot.pack.KnowledgeSnapshotVerificationManifest;

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
