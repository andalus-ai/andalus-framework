package tech.kayys.wayang.harness.controlplane;

/**
 * Handle representing an active subscription to control-plane events.
 */
public interface EventSubscription {

    void cancel();

    boolean isActive();
}
