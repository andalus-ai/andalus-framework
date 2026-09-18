package tech.kayys.wayang.knowledge.snapshot.artifact;

import java.util.Optional;

/**
 * Defines the contract for knowledge evidence artifact store operations in the Wayang framework.
 */


public interface KnowledgeEvidenceArtifactStore {
    KnowledgeEvidenceArtifactId put(KnowledgeEvidenceArtifact artifact);
    Optional<KnowledgeEvidenceArtifact> get(KnowledgeEvidenceArtifactId id);
    boolean exists(KnowledgeEvidenceArtifactId id);
    void delete(KnowledgeEvidenceArtifactId id);
    long size();
    void clear();
}
