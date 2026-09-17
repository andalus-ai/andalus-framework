package tech.kayys.wayang.harness.capability;

import java.util.Map;
import java.util.Objects;

/**
 * Contextual request representing an attempt to invoke a capability.
 */
public record CapabilityRequest(
        String capabilityId,
        String operation,
        Map<String, Object> arguments
) {

    public CapabilityRequest {
        Objects.requireNonNull(capabilityId, "capabilityId");
        arguments = arguments == null ? Map.of() : Map.copyOf(arguments);
    }

    public static CapabilityRequest of(String capabilityId) {
        return new CapabilityRequest(capabilityId, null, Map.of());
    }

    public static CapabilityRequest of(String capabilityId, String operation) {
        return new CapabilityRequest(capabilityId, operation, Map.of());
    }

    public static CapabilityRequest of(String capabilityId, String operation, Map<String, Object> arguments) {
        return new CapabilityRequest(capabilityId, operation, arguments);
    }
}
