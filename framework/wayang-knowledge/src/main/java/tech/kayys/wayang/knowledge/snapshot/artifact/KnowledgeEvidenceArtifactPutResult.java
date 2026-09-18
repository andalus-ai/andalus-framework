package tech.kayys.wayang.knowledge.snapshot.artifact;

/**
 * Represents a knowledge evidence artifact put result.
 *
 * <p>Its components capture `artifact id`, `created`, `deduplicated`, `size`.</p>
 *
 * @param artifactId the artifact id
 * @param created the created
 * @param deduplicated the deduplicated
 * @param size the size
 */


public record KnowledgeEvidenceArtifactPutResult(
        KnowledgeEvidenceArtifactId artifactId,
        boolean created,
        boolean deduplicated,
        long size
) {
}
