package tech.kayys.wayang.knowledge.snapshot.artifact;

/**
 * Represents a knowledge snapshot evidence package artifact.
 *
 * <p>Its components capture `artifact id`, `package id`, `snapshot id`, `manifest artifact id`, `seal artifact id`.</p>
 *
 * @param artifactId the artifact id
 * @param packageId the package id
 * @param snapshotId the snapshot id
 * @param manifestArtifactId the manifest artifact id
 * @param sealArtifactId the seal artifact id
 */


public record KnowledgeSnapshotEvidencePackageArtifact(
        KnowledgeEvidenceArtifactId artifactId,
        String packageId,
        String snapshotId,
        KnowledgeEvidenceArtifactId manifestArtifactId,
        KnowledgeEvidenceArtifactId sealArtifactId
) {
}
