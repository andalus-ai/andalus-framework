package tech.kayys.wayang.harness.scheduling.cancellation;

/**
 * Operating modes for execution cancellation.
 */
public enum CancellationMode {
    IMMEDIATE,
    GRACEFUL,
    DRAIN
}
