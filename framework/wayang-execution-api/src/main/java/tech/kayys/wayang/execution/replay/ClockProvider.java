package tech.kayys.wayang.execution.replay;

import java.time.Instant;

/**
 * Virtual time provider guaranteeing deterministic clock readings during replay.
 */
public interface ClockProvider {

    Instant now();

    Instant monotonicTime();
}
