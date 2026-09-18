package tech.kayys.wayang.harness.fabric;

import java.time.Instant;
import java.util.Objects;

/**
 * Health and liveness heartbeat signal transmitted periodically by workers.
 */
public record WorkerHeartbeat(
        WorkerId workerId,
        Instant timestamp,
        WorkerStatus status,
        WorkerResources resources,
        WorkerHealth health
) {

    public WorkerHeartbeat {
        Objects.requireNonNull(workerId, "WorkerId cannot be null");
        timestamp = timestamp != null ? timestamp : Instant.now();
        status = status != null ? status : WorkerStatus.READY;
        resources = resources != null ? resources : WorkerResources.of(4, 8192);
        health = health != null ? health : WorkerHealth.ok();
    }

    public static WorkerHeartbeat of(WorkerId workerId, WorkerStatus status, WorkerResources resources) {
        return new WorkerHeartbeat(workerId, Instant.now(), status, resources, WorkerHealth.ok());
    }
}
