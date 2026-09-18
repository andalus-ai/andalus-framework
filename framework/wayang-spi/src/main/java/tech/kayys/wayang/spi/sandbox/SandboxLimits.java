package tech.kayys.wayang.spi.sandbox;

import java.time.Duration;

public record SandboxLimits(
        Long cpuMillis,
        Long memoryBytes,
        Long diskBytes,
        Long processCount,
        Duration executionTimeout,
        Long outputBytes,
        Long fileCount
) {

    public SandboxLimits(
            Long cpuMillis,
            Long memoryBytes,
            Long diskBytes,
            Long processCount,
            Duration executionTimeout) {
        this(
                cpuMillis,
                memoryBytes,
                diskBytes,
                processCount,
                executionTimeout,
                -1L,
                -1L
        );
    }

    public SandboxLimits {
        validateConstraint(cpuMillis, "cpuMillis");
        validateConstraint(memoryBytes, "memoryBytes");
        validateConstraint(diskBytes, "diskBytes");
        validateConstraint(processCount, "processCount");
        validateConstraint(outputBytes, "outputBytes");
        validateConstraint(fileCount, "fileCount");

        if (executionTimeout != null && executionTimeout.isNegative()) {
            throw new IllegalArgumentException(
                    "executionTimeout must not be negative"
            );
        }
    }

    public static SandboxLimits unlimited() {
        return new SandboxLimits(
                -1L,
                -1L,
                -1L,
                -1L,
                null,
                -1L,
                -1L
        );
    }

    public boolean hasCpuLimit() {
        return cpuMillis != null && cpuMillis >= 0;
    }

    public boolean hasMemoryLimit() {
        return memoryBytes != null && memoryBytes >= 0;
    }

    public boolean hasDiskLimit() {
        return diskBytes != null && diskBytes >= 0;
    }

    public boolean hasProcessLimit() {
        return processCount != null && processCount >= 0;
    }

    public boolean hasExecutionTimeout() {
        return executionTimeout != null;
    }

    public boolean hasOutputLimit() {
        return outputBytes != null && outputBytes >= 0;
    }

    public boolean hasFileCountLimit() {
        return fileCount != null && fileCount >= 0;
    }

    private static void validateConstraint(Long value, String name) {
        if (value != null && value < -1) {
            throw new IllegalArgumentException(name + " must be >= -1");
        }
    }
}
