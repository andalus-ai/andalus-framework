package tech.kayys.wayang.spi.sandbox.artifact;

public record ArtifactDownloadRequest(
        String sandboxId,
        String executionId,
        String tenantId,
        String artifactId,
        long maxBytes
) {
    public ArtifactDownloadRequest {
        if (sandboxId == null || sandboxId.isBlank()) throw new IllegalArgumentException("sandboxId must not be blank");
        if (executionId == null || executionId.isBlank()) throw new IllegalArgumentException("executionId must not be blank");
        if (artifactId == null || artifactId.isBlank()) throw new IllegalArgumentException("artifactId must not be blank");
        if (maxBytes == 0 || maxBytes < -1) throw new IllegalArgumentException("maxBytes must be -1 or > 0");
    }
}
