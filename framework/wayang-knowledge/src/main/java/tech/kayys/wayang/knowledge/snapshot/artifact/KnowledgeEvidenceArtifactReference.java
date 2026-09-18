package tech.kayys.wayang.knowledge.snapshot.artifact;

import java.util.Map;

/**
 * Represents a knowledge evidence artifact reference.
 *
 * <p>Its components capture `artifact id`, `role`, `required for verification`, `metadata`.</p>
 *
 * @param artifactId the artifact id
 * @param role the role
 * @param requiredForVerification the required for verification
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactReference(
        KnowledgeEvidenceArtifactId artifactId,
        String role,
        boolean requiredForVerification,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceArtifactReference {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
