package tech.kayys.andalus.harness.observability.event;

/**
 * Handle representing an active real-time event subscription.
 */
public interface EventSubscription extends AutoCloseable {

    boolean isActive();

    void unsubscribe();

    @Override
    default void close() {
        unsubscribe();
    }
}
