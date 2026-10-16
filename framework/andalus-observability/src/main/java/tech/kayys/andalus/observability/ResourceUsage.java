package tech.kayys.andalus.observability;

import java.util.Map;

public record ResourceUsage(
        long cpuTimeMillis,
        long memoryBytes,
        long networkBytesIn,
        long networkBytesOut,
        long filesystemBytesRead,
        long filesystemBytesWritten,
        Map<String, Object> additional
) {

    public ResourceUsage {
        additional = additional == null
                ? Map.of()
                : Map.copyOf(additional);
    }
}
