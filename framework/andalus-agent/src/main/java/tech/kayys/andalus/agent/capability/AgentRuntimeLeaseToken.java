package tech.kayys.andalus.agent.capability;

import java.util.Objects;

/**
 * Fencing identity for one owner of an agent runtime registration.
 *
 * <p>The generation must be monotonically increased by the backing store
 * whenever ownership is acquired or taken over. A token from an older
 * generation must never renew, update, or revoke its successor.</p>
 */
public record AgentRuntimeLeaseToken(
        String tenantId,
        String agentId,
        String instanceId,
        String ownerId,
        long generation) {

    public AgentRuntimeLeaseToken {
        tenantId = requireText(tenantId, "tenantId");
        agentId = requireText(agentId, "agentId");
        instanceId = requireText(instanceId, "instanceId");
        ownerId = requireText(ownerId, "ownerId");
        if (generation < 1) {
            throw new IllegalArgumentException("generation must be positive");
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
