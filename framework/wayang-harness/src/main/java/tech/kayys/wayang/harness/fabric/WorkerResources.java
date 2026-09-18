package tech.kayys.wayang.harness.fabric;

/**
 * Real-time resource metrics reported by a worker.
 */
public record WorkerResources(
        long totalCpuCores,
        long allocatedCpuCores,
        long totalMemoryMb,
        long allocatedMemoryMb
) {

    public long availableCpu() {
        return Math.max(0, totalCpuCores - allocatedCpuCores);
    }

    public long availableMemory() {
        return Math.max(0, totalMemoryMb - allocatedMemoryMb);
    }

    public static WorkerResources of(long cpu, long memory) {
        return new WorkerResources(cpu, 0L, memory, 0L);
    }
}
