package tech.kayys.wayang.spi.sandbox.artifact;

public record ArtifactInspectionResult(boolean allowed, String reason) {
    public static ArtifactInspectionResult allow() { return new ArtifactInspectionResult(true, null); }
    public static ArtifactInspectionResult deny(String reason) { return new ArtifactInspectionResult(false, reason); }
}
