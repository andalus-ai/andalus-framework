package tech.kayys.andalus.execution.replay;

/**
 * Safety classification for replay requests.
 */
public enum ReplaySafety {
    SAFE,
    READ_ONLY,
    SIMULATED,
    LIVE_HAZARDOUS
}
