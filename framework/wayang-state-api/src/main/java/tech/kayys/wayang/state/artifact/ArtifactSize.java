package tech.kayys.wayang.state.artifact;

public record ArtifactSize(long bytes) {
    public static ArtifactSize of(long bytes) {
        return new ArtifactSize(bytes);
    }
}
