package tech.kayys.wayang.spi.sandbox.observability;

public record NetworkMetrics(
        long bytesSent,
        long bytesReceived,
        long connectionsOpened,
        long connectionsFailed
) {
    public static NetworkMetrics empty() {
        return new NetworkMetrics(-1, -1, -1, -1);
    }
}
