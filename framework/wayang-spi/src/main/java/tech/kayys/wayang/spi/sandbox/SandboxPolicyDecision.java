package tech.kayys.wayang.spi.sandbox;

public sealed interface SandboxPolicyDecision
        permits SandboxPolicyDecision.Allow,
                SandboxPolicyDecision.Deny {

    record Allow(
            ExecutionIsolationProfile profile
    ) implements SandboxPolicyDecision {
    }

    record Deny(
            String reason
    ) implements SandboxPolicyDecision {
    }

    static Allow allow(ExecutionIsolationProfile profile) {
        return new Allow(profile);
    }

    static Deny deny(String reason) {
        return new Deny(reason);
    }
}
