package tech.kayys.wayang.spi.sandbox.artifact;

public interface ArtifactPolicy {
    String id();
    default int priority() { return 100; }
    default boolean enabled() { return true; }
    ArtifactPolicyDecision evaluate(ArtifactPolicyContext context);
}
