package tech.kayys.wayang.spi.sandbox.artifact;

public sealed interface ArtifactPolicyDecision
        permits ArtifactPolicyDecision.Allow,
                ArtifactPolicyDecision.Deny {

    record Allow() implements ArtifactPolicyDecision {}
    record Deny(String reason) implements ArtifactPolicyDecision {}

    static Allow allow() { return new Allow(); }
    static Deny deny(String reason) { return new Deny(reason); }
}
