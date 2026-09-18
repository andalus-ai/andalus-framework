package tech.kayys.wayang.harness.scheduling.clock;

import java.time.Instant;

/**
 * Universal clock abstraction enabling virtual and mockable time in tests and simulations.
 */
public interface WayangClock {

    Instant now();

    static WayangClock system() {
        return Instant::now;
    }
}
