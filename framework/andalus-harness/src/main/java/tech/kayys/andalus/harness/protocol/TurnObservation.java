package tech.kayys.andalus.harness.protocol;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record TurnObservation(
        ObservationId id,
        TurnId turnId,
        long sequence,
        ObservationType type,
        Instant timestamp,
        Object value,
        Map<String, Object> metadata
) {

    public TurnObservation {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(turnId, "turnId");
        Objects.requireNonNull(type, "type");

        if (sequence < 0) {
            throw new IllegalArgumentException(
                    "Observation sequence must be >= 0");
        }

        timestamp = timestamp != null
                ? timestamp
                : Instant.now();

        metadata = metadata != null
                ? Map.copyOf(metadata)
                : Map.of();
    }

    public static TurnObservation of(
            TurnId turnId,
            ObservationType type,
            Object value
    ) {
        return new TurnObservation(
                ObservationId.generate(),
                turnId,
                0L,
                type,
                Instant.now(),
                value,
                Map.of()
        );
    }
}
