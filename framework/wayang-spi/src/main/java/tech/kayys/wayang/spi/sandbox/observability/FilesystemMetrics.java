package tech.kayys.wayang.spi.sandbox.observability;

public record FilesystemMetrics(
        long workspaceBytes,
        long inputBytes,
        long outputBytes,
        long fileCount
) {
    public static FilesystemMetrics empty() {
        return new FilesystemMetrics(-1, -1, -1, -1);
    }
}
