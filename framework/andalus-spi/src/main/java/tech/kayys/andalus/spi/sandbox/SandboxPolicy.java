package tech.kayys.andalus.spi.sandbox;

public interface SandboxPolicy {

    String id();

    default int priority() {
        return 100;
    }

    default boolean enabled() {
        return true;
    }

    SandboxPolicyDecision evaluate(SandboxPolicyContext context);
}
