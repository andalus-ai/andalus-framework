package tech.kayys.andalus.execution.provenance;

import java.time.Instant;
import java.util.Objects;

/**
 * Time interval bounding temporal graph queries.
 */
public record TemporalInterval(Instant start, Instant end) {
    public TemporalInterval {
        Objects.requireNonNull(start, "start cannot be null");
        Objects.requireNonNull(end, "end cannot be null");
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end cannot be before start");
        }
    }

    public boolean contains(Instant instant) {
        if (instant == null) {
            return false;
        }
        return !instant.isBefore(start) && !instant.isAfter(end);
    }

    public static TemporalInterval between(Instant start, Instant end) {
        return new TemporalInterval(start, end);
    }
}
