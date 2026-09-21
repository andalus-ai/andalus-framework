package tech.kayys.andalus.harness.artifact;

/**
 * Universal interface representing an immutable artifact in Andalus Harness.
 */
public interface Artifact {

    ArtifactId id();

    ArtifactType type();

    ArtifactDescriptor descriptor();

    ArtifactDigest digest();

    ArtifactMetadata metadata();

    ArtifactProvenance provenance();
}
