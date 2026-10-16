package tech.kayys.andalus.execution.turn;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record TurnObservation(
        String id,
        ObservationType type,
        String source,
        Object value,
        Instant observedAt,
        Map<String, Object> metadata
) {

    public TurnObservation {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(source, "source cannot be null");
        Objects.requireNonNull(observedAt, "observedAt cannot be null");

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public static TurnObservation of(String id, ObservationType type, String source, Object value) {
        return new TurnObservation(id, type, source, value, Instant.now(), Map.of());
    }
}
