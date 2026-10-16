package tech.kayys.andalus.observability;

import java.util.Map;

public interface ObservabilityPublisher {

    void publish(ObservationEvent event);

    default void publish(
            String executionId,
            String traceId,
            ObservabilityEventType type) {

        publish(new ObservationEvent(
                null,
                executionId,
                traceId,
                type != null ? type.name() : "UNKNOWN",
                null,
                Map.of()
        ));
    }

    default void publish(
            String executionId,
            String traceId,
            ObservabilityEventType type,
            Map<String, Object> attributes) {

        publish(new ObservationEvent(
                null,
                executionId,
                traceId,
                type != null ? type.name() : "UNKNOWN",
                null,
                attributes
        ));
    }

    default boolean enabled() {
        return true;
    }
}
