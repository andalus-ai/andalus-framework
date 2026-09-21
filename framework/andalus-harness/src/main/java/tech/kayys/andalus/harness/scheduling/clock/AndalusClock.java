package tech.kayys.andalus.harness.scheduling.clock;

import java.time.Instant;

/**
 * Universal clock abstraction enabling virtual and mockable time in tests and simulations.
 */
public interface AndalusClock {

    Instant now();

    static AndalusClock system() {
        return Instant::now;
    }
}
