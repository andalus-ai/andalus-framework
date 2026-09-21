package tech.kayys.andalus.execution.resource.policy;

import java.util.Objects;

/**
 * Policy evaluation decision for virtual resource requests.
 */
public record ResourcePolicyDecision(boolean allowed, String reason) {
    public ResourcePolicyDecision {
        reason = reason == null ? "" : reason;
    }

    public static ResourcePolicyDecision allow() {
        return new ResourcePolicyDecision(true, "Allowed by resource policy");
    }

    public static ResourcePolicyDecision deny(String reason) {
        return new ResourcePolicyDecision(false, reason);
    }
}
