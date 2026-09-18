package tech.kayys.wayang.spi.sandbox.artifact;

public record ArtifactTransferLimits(
        long maxInputBytes,
        long maxOutputBytes,
        long maxArtifactBytes,
        int maxArtifactCount
) {
    public static ArtifactTransferLimits unlimited() {
        return new ArtifactTransferLimits(-1, -1, -1, -1);
    }
}
