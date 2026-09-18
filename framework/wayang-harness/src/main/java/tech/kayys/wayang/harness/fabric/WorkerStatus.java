package tech.kayys.wayang.harness.fabric;

/**
 * Standard lifecycle status of a distributed execution worker.
 */
public enum WorkerStatus {
    REGISTERING,
    REGISTERED,
    READY,
    BUSY,
    DRAINING,
    OFFLINE,
    UNHEALTHY;

    public boolean isAvailable() {
        return this == REGISTERED || this == READY;
    }
}
