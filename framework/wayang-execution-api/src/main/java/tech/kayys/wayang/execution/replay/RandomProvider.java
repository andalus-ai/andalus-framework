package tech.kayys.wayang.execution.replay;

/**
 * Deterministic pseudo-random number generator for replay and simulation.
 */
public interface RandomProvider {

    long nextLong();

    double nextDouble();
}
