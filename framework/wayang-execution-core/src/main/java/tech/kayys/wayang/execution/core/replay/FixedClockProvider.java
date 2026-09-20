package tech.kayys.wayang.execution.core.replay;

import tech.kayys.wayang.execution.replay.ClockProvider;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Deterministic clock provider holding a fixed or advanceable virtual time.
 */
public class FixedClockProvider implements ClockProvider {

    private final AtomicReference<Instant> current;

    public FixedClockProvider(Instant fixedInstant) {
        this.current = new AtomicReference<>(Objects.requireNonNull(fixedInstant, "fixedInstant cannot be null"));
    }

    public FixedClockProvider() {
        this(Instant.parse("2026-09-20T08:00:00Z"));
    }

    @Override
    public Instant now() {
        return current.get();
    }

    @Override
    public Instant monotonicTime() {
        return current.get();
    }

    public void advanceSeconds(long seconds) {
        current.updateAndGet(prev -> prev.plusSeconds(seconds));
    }
}
