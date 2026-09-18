package tech.kayys.wayang.knowledge.snapshot.artifact;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence artifact metadata.
 *
 * <p>Its components capture `artifact id`, `media type`, `size`, `created at`, `producer`, and other values.</p>
 *
 * @param artifactId the artifact id
 * @param mediaType the media type
 * @param size the size
 * @param createdAt the created at
 * @param producer the producer
 * @param schemaVersion the schema version
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactMetadata(
        KnowledgeEvidenceArtifactId artifactId,
        String mediaType,
        long size,
        Instant createdAt,
        String producer,
        String schemaVersion,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceArtifactMetadata {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
