package tech.kayys.wayang.spi.sandbox;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public record SandboxPolicyContext(
        String executionId,
        String tenantId,
        String userId,
        String agentId,
        String correlationId,
        ExecutionIsolationProfile requestedProfile,
        Optional<String> toolName,
        Optional<String> capabilityId,
        Instant deadline,
        Map<String, Object> attributes
) {

    public SandboxPolicyContext {
        executionId = requireText(executionId, "executionId");
        tenantId = requireText(tenantId, "tenantId");

        requestedProfile = requestedProfile == null
                ? ExecutionIsolationProfile.none()
                : requestedProfile;

        toolName = toolName == null
                ? Optional.empty()
                : toolName;

        capabilityId = capabilityId == null
                ? Optional.empty()
                : capabilityId;

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }
}
