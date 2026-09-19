package tech.kayys.wayang.execution.limits;

/**
 * CPU execution limits.
 */
public record CpuLimit(
        double maxCores,
        int cpuShares
) {

    public static CpuLimit of(double maxCores) {
        return new CpuLimit(maxCores, 1024);
    }

    public static CpuLimit unlimited() {
        return new CpuLimit(Double.MAX_VALUE, 1024);
    }
}
