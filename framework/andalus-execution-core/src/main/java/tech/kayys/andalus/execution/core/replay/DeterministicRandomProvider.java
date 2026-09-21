package tech.kayys.andalus.execution.core.replay;

import tech.kayys.andalus.execution.replay.RandomProvider;

import java.util.Random;

/**
 * Deterministic pseudo-random number generator for reproducible execution replay.
 */
public class DeterministicRandomProvider implements RandomProvider {

    private final Random random;

    public DeterministicRandomProvider(long seed) {
        this.random = new Random(seed);
    }

    public DeterministicRandomProvider() {
        this(42L);
    }

    @Override
    public synchronized long nextLong() {
        return random.nextLong();
    }

    @Override
    public synchronized double nextDouble() {
        return random.nextDouble();
    }
}
