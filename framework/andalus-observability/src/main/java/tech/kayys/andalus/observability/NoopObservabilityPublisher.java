package tech.kayys.andalus.observability;

public final class NoopObservabilityPublisher
        implements ObservabilityPublisher {

    public static final NoopObservabilityPublisher INSTANCE = new NoopObservabilityPublisher();

    @Override
    public void publish(ObservationEvent event) {
        // intentionally empty
    }

    @Override
    public boolean enabled() {
        return false;
    }
}
