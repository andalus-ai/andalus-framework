package tech.kayys.andalus.harness.controlplane;

/**
 * Functional handler processing incoming control-plane event envelopes.
 */
@FunctionalInterface
public interface ControlPlaneEventHandler {

    void onEvent(EventEnvelope envelope);
}
