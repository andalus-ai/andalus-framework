package tech.kayys.andalus.resilience;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


/**
 * Circuit Breaker Configuration
 */
public record CircuitBreakerConfig(
    int failureThreshold,
    int successThreshold,
    long timeoutMs,
    long waitDurationMs,
    int maxConcurrentRequests
) {
    public static CircuitBreakerConfig defaults() {
        return new CircuitBreakerConfig(5, 3, 30000, 60000, 10);
    }
}
