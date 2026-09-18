package tech.kayys.wayang.spi.sandbox;

import java.time.Duration;

public record SandboxLimits(
        Long cpuMillis,
        Long memoryBytes,
        Long diskBytes,
        Long processCount,
        Duration executionTimeout
) {

    public SandboxLimits {
        validateNonNegative(cpuMillis, "cpuMillis");
        validateNonNegative(memoryBytes, "memoryBytes");
        validateNonNegative(diskBytes, "diskBytes");
        validateNonNegative(processCount, "processCount");

        if (executionTimeout != null &&
                (executionTimeout.isZero() || executionTimeout.isNegative())) {

            throw new IllegalArgumentException(
                    "executionTimeout must be positive"
            );
        }
    }

    public static SandboxLimits unlimited() {
        return new SandboxLimits(
                null,
                null,
                null,
                null,
                null
        );
    }

    private static void validateNonNegative(
            Long value,
            String name) {

        if (value != null && value < 0) {
            throw new IllegalArgumentException(
                    name + " cannot be negative"
            );
        }
    }
}
