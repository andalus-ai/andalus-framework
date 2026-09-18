package tech.kayys.wayang.harness.fabric;

import java.time.Instant;

/**
 * Health assessment of an active worker.
 */
public record WorkerHealth(
        boolean healthy,
        Instant lastHeartbeat,
        String statusMessage
) {

    public static WorkerHealth ok() {
        return new WorkerHealth(true, Instant.now(), "Worker operational");
    }

    public static WorkerHealth unhealthy(String message) {
        return new WorkerHealth(false, Instant.now(), message != null ? message : "Health check failed");
    }
}
