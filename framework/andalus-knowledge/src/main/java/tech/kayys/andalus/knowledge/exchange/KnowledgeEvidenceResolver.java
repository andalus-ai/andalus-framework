package tech.kayys.andalus.knowledge.exchange;

import tech.kayys.andalus.knowledge.snapshot.artifact.KnowledgeEvidenceArtifact;
import tech.kayys.andalus.knowledge.snapshot.artifact.KnowledgeEvidenceArtifactId;

import java.util.Optional;

/**
 * Defines the contract for knowledge evidence resolver operations in the Wayang framework.
 */


public interface KnowledgeEvidenceResolver {
    Optional<KnowledgeEvidenceArtifact> resolve(KnowledgeEvidenceArtifactId artifactId);
}
