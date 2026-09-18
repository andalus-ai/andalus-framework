package tech.kayys.wayang.harness.fabric;

import java.time.Duration;
import java.util.Objects;

/**
 * Immutable reference record implementing {@link ExecutionWorker}.
 */
public record DefaultExecutionWorker(
        WorkerId id,
        WorkerCapabilities capabilities,
        WorkerStatus status,
        WorkerResources resources,
        WorkerHealth health,
        WorkerLease lease
) implements ExecutionWorker {

    public DefaultExecutionWorker {
        Objects.requireNonNull(id, "WorkerId cannot be null");
        capabilities = capabilities != null ? capabilities : WorkerCapabilities.standard();
        status = status != null ? status : WorkerStatus.REGISTERED;
        resources = resources != null ? resources : WorkerResources.of(4, 8192);
        health = health != null ? health : WorkerHealth.ok();
        lease = lease != null ? lease : WorkerLease.create(Duration.ofMinutes(5));
    }

    public static DefaultExecutionWorker create(WorkerId id) {
        return new DefaultExecutionWorker(
                id,
                WorkerCapabilities.standard(),
                WorkerStatus.REGISTERED,
                WorkerResources.of(4, 8192),
                WorkerHealth.ok(),
                WorkerLease.create(Duration.ofMinutes(5))
        );
    }

    public DefaultExecutionWorker withStatus(WorkerStatus newStatus) {
        return new DefaultExecutionWorker(id, capabilities, newStatus, resources, health, lease);
    }

    public DefaultExecutionWorker withHealth(WorkerHealth newHealth) {
        return new DefaultExecutionWorker(id, capabilities, status, resources, newHealth, lease);
    }

    public DefaultExecutionWorker withLease(WorkerLease newLease) {
        return new DefaultExecutionWorker(id, capabilities, status, resources, health, newLease);
    }
}
