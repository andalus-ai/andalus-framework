package tech.kayys.andalus.agent.capability;

import java.time.Duration;
import java.util.Objects;

/** Request to acquire or take over a tenant-scoped agent runtime registration. */
public record AgentRuntimeRegistrationRequest(
        String tenantId,
        String agentId,
        String instanceId,
        String ownerId,
        Duration leaseDuration) {

    public AgentRuntimeRegistrationRequest {
        tenantId = requireText(tenantId, "tenantId");
        agentId = requireText(agentId, "agentId");
        instanceId = requireText(instanceId, "instanceId");
        ownerId = requireText(ownerId, "ownerId");
        Objects.requireNonNull(leaseDuration, "leaseDuration must not be null");
        if (leaseDuration.isZero() || leaseDuration.isNegative()) {
            throw new IllegalArgumentException("leaseDuration must be positive");
        }
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }
}
