package tech.kayys.andalus.execution.replay;

/**
 * Determinism classification for an execution or replay step.
 */
public enum DeterminismLevel {
    DETERMINISTIC,
    BOUNDED_NONDETERMINISTIC,
    STOCHASTIC,
    NONDETERMINISTIC
}
