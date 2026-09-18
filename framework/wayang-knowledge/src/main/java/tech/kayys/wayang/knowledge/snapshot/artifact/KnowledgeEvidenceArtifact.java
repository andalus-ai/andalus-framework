package tech.kayys.wayang.knowledge.snapshot.artifact;

/**
 * Represents a knowledge evidence artifact.
 *
 * <p>Its components capture `metadata`, `content`.</p>
 *
 * @param metadata the metadata
 * @param content the content
 */


public record KnowledgeEvidenceArtifact(
        KnowledgeEvidenceArtifactMetadata metadata,
        byte[] content
) {
    public KnowledgeEvidenceArtifact {
        if (metadata == null) {
            throw new IllegalArgumentException("metadata is required");
        }
        content = content == null ? new byte[0] : content.clone();
        if (metadata.size() != content.length) {
            throw new IllegalArgumentException("Artifact size does not match content length");
        }
    }

    @Override
    public byte[] content() {
        return content.clone();
    }
}
