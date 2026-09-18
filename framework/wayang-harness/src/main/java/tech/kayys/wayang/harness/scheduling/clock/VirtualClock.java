package tech.kayys.wayang.harness.scheduling.clock;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Controllable in-memory virtual clock for testing without real-time delays.
 */
public class VirtualClock implements WayangClock {

    private final AtomicReference<Instant> current;

    public VirtualClock(Instant initialTime) {
        this.current = new AtomicReference<>(Objects.requireNonNull(initialTime, "initialTime"));
    }

    public static VirtualClock atEpoch() {
        return new VirtualClock(Instant.EPOCH);
    }

    @Override
    public Instant now() {
        return current.get();
    }

    public void advance(Duration duration) {
        if (duration != null) {
            current.updateAndGet(t -> t.plus(duration));
        }
    }

    public void set(Instant newTime) {
        current.set(Objects.requireNonNull(newTime, "newTime"));
    }
}
