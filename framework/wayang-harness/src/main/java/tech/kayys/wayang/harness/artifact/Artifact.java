package tech.kayys.wayang.harness.artifact;

/**
 * Universal interface representing an immutable artifact in Wayang Harness.
 */
public interface Artifact {

    ArtifactId id();

    ArtifactType type();

    ArtifactDescriptor descriptor();

    ArtifactDigest digest();

    ArtifactMetadata metadata();

    ArtifactProvenance provenance();
}
