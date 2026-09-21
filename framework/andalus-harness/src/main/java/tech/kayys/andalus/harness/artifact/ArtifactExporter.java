package tech.kayys.andalus.harness.artifact;

/**
 * SPI for exporting artifacts to external targets (e.g. S3, Git, local filesystem).
 */
public interface ArtifactExporter {

    ExportHandle export(ArtifactId artifactId, ExportRequest request);
}
