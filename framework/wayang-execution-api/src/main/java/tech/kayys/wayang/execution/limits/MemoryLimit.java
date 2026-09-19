package tech.kayys.wayang.execution.limits;

/**
 * Memory consumption limits.
 */
public record MemoryLimit(
        long maxBytes,
        long swapBytes
) {

    public static MemoryLimit megabytes(long mb) {
        return new MemoryLimit(mb * 1024L * 1024L, 0L);
    }

    public static MemoryLimit gigabytes(long gb) {
        return new MemoryLimit(gb * 1024L * 1024L * 1024L, 0L);
    }

    public static MemoryLimit unlimited() {
        return new MemoryLimit(Long.MAX_VALUE, 0L);
    }
}
