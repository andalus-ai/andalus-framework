package tech.kayys.wayang.spi.sandbox;

public sealed interface NetworkDecision
        permits NetworkDecision.Allowed,
                NetworkDecision.Denied {

    record Allowed(
            String providerId
    ) implements NetworkDecision {
    }

    record Denied(
            String reason
    ) implements NetworkDecision {
    }

    static Allowed allowed(String providerId) {
        return new Allowed(providerId);
    }

    static Denied denied(String reason) {
        return new Denied(reason);
    }
}
