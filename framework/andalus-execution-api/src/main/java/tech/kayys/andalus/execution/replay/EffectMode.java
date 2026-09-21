package tech.kayys.andalus.execution.replay;

/**
 * Modes controlling how side effects are handled during replay.
 */
public enum EffectMode {
    REAL,
    VIRTUAL,
    RECORDED,
    BLOCKED
}
