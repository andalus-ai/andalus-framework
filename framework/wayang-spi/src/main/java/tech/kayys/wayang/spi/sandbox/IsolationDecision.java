package tech.kayys.wayang.spi.sandbox;

public sealed interface IsolationDecision
        permits IsolationDecision.Allowed,
                IsolationDecision.Denied {

    record Allowed(
            SandboxType sandboxType,
            String providerId
    ) implements IsolationDecision {
    }

    record Denied(
            String reason
    ) implements IsolationDecision {
    }

    static Allowed allowed(
            SandboxType sandboxType,
            String providerId) {

        return new Allowed(
                sandboxType,
                providerId
        );
    }

    static Denied denied(String reason) {
        return new Denied(reason);
    }
}
