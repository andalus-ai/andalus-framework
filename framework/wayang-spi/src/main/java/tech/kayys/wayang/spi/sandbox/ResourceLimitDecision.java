package tech.kayys.wayang.spi.sandbox;

public sealed interface ResourceLimitDecision
        permits ResourceLimitDecision.Allowed,
                ResourceLimitDecision.Denied {

    record Allowed(
            String providerId
    ) implements ResourceLimitDecision {
    }

    record Denied(
            String reason
    ) implements ResourceLimitDecision {
    }

    static Allowed allowed(String providerId) {
        return new Allowed(providerId);
    }

    static Denied denied(String reason) {
        return new Denied(reason);
    }
}
