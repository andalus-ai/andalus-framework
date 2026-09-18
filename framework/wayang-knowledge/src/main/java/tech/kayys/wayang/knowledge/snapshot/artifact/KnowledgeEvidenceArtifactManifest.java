package tech.kayys.wayang.knowledge.snapshot.artifact;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence artifact manifest.
 *
 * <p>Its components capture `artifact id`, `artifact type`, `schema version`, `size`, `references`, and other values.</p>
 *
 * @param artifactId the artifact id
 * @param artifactType the artifact type
 * @param schemaVersion the schema version
 * @param size the size
 * @param references the references
 * @param dependencies the dependencies
 * @param createdAt the created at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactManifest(
        KnowledgeEvidenceArtifactId artifactId,
        String artifactType,
        String schemaVersion,
        long size,
        List<KnowledgeEvidenceArtifactReference> references,
        List<KnowledgeEvidenceArtifactDependency> dependencies,
        Instant createdAt,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceArtifactManifest {
        references = references == null ? List.of() : List.copyOf(references);
        dependencies = dependencies == null ? List.of() : List.copyOf(dependencies);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
