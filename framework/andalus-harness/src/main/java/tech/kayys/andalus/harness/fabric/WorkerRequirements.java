package tech.kayys.andalus.harness.fabric;

import java.util.Objects;

/** Resource and runtime capabilities required by one schedulable execution task. */
public record WorkerRequirements(
        String environment,
        String executor,
        boolean requiresGpu,
        long cpuCores,
        long memoryMb
) {
    public WorkerRequirements {
        if (cpuCores < 0 || memoryMb < 0) {
            throw new IllegalArgumentException("Resource requirements cannot be negative");
        }
        if (environment != null && environment.isBlank()) {
            throw new IllegalArgumentException("environment cannot be blank");
        }
        if (executor != null && executor.isBlank()) {
            throw new IllegalArgumentException("executor cannot be blank");
        }
    }

    public static WorkerRequirements minimal() {
        return new WorkerRequirements(null, null, false, 1, 256);
    }

    public boolean isSatisfiedBy(ExecutionWorker worker) {
        Objects.requireNonNull(worker, "worker cannot be null");
        WorkerCapabilities capabilities = worker.capabilities();
        WorkerResources resources = worker.resources();
        return capabilities.canExecute(environment, executor, requiresGpu)
                && capabilities.maxCpuCores() >= cpuCores
                && capabilities.maxMemoryMb() >= memoryMb
                && resources.availableCpu() >= cpuCores
                && resources.availableMemory() >= memoryMb;
    }
}
