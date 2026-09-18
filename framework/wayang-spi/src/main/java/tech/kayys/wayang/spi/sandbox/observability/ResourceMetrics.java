package tech.kayys.wayang.spi.sandbox.observability;

public record ResourceMetrics(
        long cpuMillisUsed,
        long memoryBytesUsed,
        long memoryBytesPeak,
        long diskBytesUsed,
        int processCount,
        long outputBytes,
        long fileCount
) {
    public static ResourceMetrics empty() {
        return new ResourceMetrics(-1, -1, -1, -1, -1, -1, -1);
    }
}
