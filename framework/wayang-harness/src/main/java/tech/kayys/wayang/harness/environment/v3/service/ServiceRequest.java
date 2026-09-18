package tech.kayys.wayang.harness.environment.v3.service;

import java.util.Map;
import java.util.Objects;

/**
 * Request to acquire access to an external managed service (e.g. database, browser, queue).
 */
public record ServiceRequest(
        String serviceType,
        String serviceName,
        Map<String, String> parameters
) {
    public ServiceRequest {
        Objects.requireNonNull(serviceType, "serviceType");
        Objects.requireNonNull(serviceName, "serviceName");
        parameters = parameters != null ? Map.copyOf(parameters) : Map.of();
    }

    public static ServiceRequest of(String serviceType, String serviceName) {
        return new ServiceRequest(serviceType, serviceName, Map.of());
    }
}
