package tech.kayys.wayang.spi.sandbox.observability;

public record ArtifactMetrics(
        long uploadedBytes,
        long downloadedBytes,
        long uploadedCount,
        long downloadedCount,
        long failedTransfers
) {
    public static ArtifactMetrics empty() {
        return new ArtifactMetrics(-1, -1, -1, -1, -1);
    }
}
