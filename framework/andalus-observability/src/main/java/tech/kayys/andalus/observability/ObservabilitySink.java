package tech.kayys.andalus.observability;

public interface ObservabilitySink {

    void publish(ObservationEvent event);

    default boolean supports(ObservationEvent event) {
        return true;
    }

    default void close() {
    }
}
