package tech.kayys.wayang.harness.scheduling.temporal;

import java.time.Instant;
import java.util.Objects;

/**
 * Hard temporal deadline after which an execution must not proceed.
 */
public record Deadline(Instant at) {
    public Deadline {
        Objects.requireNonNull(at, "at");
    }

    public boolean isExceeded(Instant now) {
        return now.isAfter(at);
    }
}
