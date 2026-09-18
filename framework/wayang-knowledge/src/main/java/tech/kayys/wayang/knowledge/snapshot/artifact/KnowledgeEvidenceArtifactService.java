package tech.kayys.wayang.knowledge.snapshot.artifact;

import java.util.Optional;

/**
 * Defines the contract for knowledge evidence artifact service operations in the Wayang framework.
 */


public interface KnowledgeEvidenceArtifactService {

    KnowledgeEvidenceArtifactPutResult put(
            byte[] content,
            String mediaType,
            String producer,
            String schemaVersion
    );

    Optional<KnowledgeEvidenceArtifact> get(
            KnowledgeEvidenceArtifactId id
    );

    boolean verify(
            KnowledgeEvidenceArtifactId id
    );
}
