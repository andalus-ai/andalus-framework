package tech.kayys.andalus.execution.replay;

/**
 * Deterministic pseudo-random number generator for replay and simulation.
 */
public interface RandomProvider {

    long nextLong();

    double nextDouble();
}
