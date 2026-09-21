package tech.kayys.andalus.health;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.Map;

/**
 * Health Result
 */
public record HealthResult(
    HealthStatus status,
    String message,
    Map<String, Object> details,
    long timestamp
) {
    public static HealthResult healthy() {
        return new HealthResult(HealthStatus.HEALTHY, null, Map.of(), System.currentTimeMillis());
    }
    
    public static HealthResult degraded(String message) {
        return new HealthResult(HealthStatus.DEGRADED, message, Map.of(), System.currentTimeMillis());
    }
    
    public static HealthResult unhealthy(String message) {
        return new HealthResult(HealthStatus.UNHEALTHY, message, Map.of(), System.currentTimeMillis());
    }
    
    public static HealthResult unknown(String message) {
        return new HealthResult(HealthStatus.UNKNOWN, message, Map.of(), System.currentTimeMillis());
    }
}
