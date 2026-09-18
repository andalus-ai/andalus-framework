package tech.kayys.wayang.knowledge.snapshot.artifact;

import java.util.Map;

/**
 * Represents a knowledge evidence artifact dependency.
 *
 * <p>Its components capture `artifact id`, `dependency id`, `required for verification`, `metadata`.</p>
 *
 * @param artifactId the artifact id
 * @param dependencyId the dependency id
 * @param requiredForVerification the required for verification
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactDependency(
        KnowledgeEvidenceArtifactId artifactId,
        KnowledgeEvidenceArtifactId dependencyId,
        boolean requiredForVerification,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceArtifactDependency {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
