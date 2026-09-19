package tech.kayys.wayang.execution.limits;

/**
 * Storage capacity and inode limits for workspace execution.
 */
public record StorageLimit(
        long maxBytes,
        int maxInodes
) {

    public static StorageLimit gigabytes(long gb) {
        return new StorageLimit(gb * 1024L * 1024L * 1024L, 100_000);
    }

    public static StorageLimit unlimited() {
        return new StorageLimit(Long.MAX_VALUE, Integer.MAX_VALUE);
    }
}
