package tech.kayys.wayang.governance.adaptation;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe default implementation of {@link DynamicPolicyAdapter}.
 */
public class DefaultDynamicPolicyAdapter implements DynamicPolicyAdapter {

    private final Map<String, CapabilityFallbackRoute> routes = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> failureCounts = new ConcurrentHashMap<>();

    @Override
    public void registerRoute(CapabilityFallbackRoute route) {
        Objects.requireNonNull(route, "route cannot be null");
        routes.put(route.originalCapability(), route);
    }

    @Override
    public void recordFailure(String capabilityUri) {
        Objects.requireNonNull(capabilityUri, "capabilityUri cannot be null");
        failureCounts.computeIfAbsent(capabilityUri, k -> new AtomicInteger(0)).incrementAndGet();
    }

    @Override
    public void recordSuccess(String capabilityUri) {
        Objects.requireNonNull(capabilityUri, "capabilityUri cannot be null");
        AtomicInteger count = failureCounts.get(capabilityUri);
        if (count != null) {
            count.set(0);
        }
    }

    @Override
    public Optional<String> resolveFallback(String failedCapability) {
        Objects.requireNonNull(failedCapability, "failedCapability cannot be null");
        CapabilityFallbackRoute route = routes.get(failedCapability);
        if (route == null || route.alternativeCapabilities().isEmpty()) {
            return Optional.empty();
        }

        AtomicInteger count = failureCounts.get(failedCapability);
        int currentFailures = count != null ? count.get() : 0;

        if (currentFailures >= route.maxConsecutiveFailures()) {
            // Select first alternative
            return Optional.of(route.alternativeCapabilities().get(0));
        }

        return Optional.empty();
    }
}
