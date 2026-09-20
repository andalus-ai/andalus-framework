package tech.kayys.wayang.execution.replay;

/**
 * Safety classification for replay requests.
 */
public enum ReplaySafety {
    SAFE,
    READ_ONLY,
    SIMULATED,
    LIVE_HAZARDOUS
}
