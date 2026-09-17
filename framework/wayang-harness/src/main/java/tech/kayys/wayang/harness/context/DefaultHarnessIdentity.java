package tech.kayys.wayang.harness.context;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable value record implementing {@link HarnessIdentity}.
 */
public record DefaultHarnessIdentity(
        String agentId,
        String tenantId,
        String userId,
        String executionId,
        String namespace
) implements HarnessIdentity {

    public DefaultHarnessIdentity {
        agentId = Objects.requireNonNull(agentId, "agentId");
        tenantId = tenantId == null ? "default" : tenantId;
        userId = userId == null ? "anonymous" : userId;
        executionId = executionId == null ? "exec-" + UUID.randomUUID() : executionId;
        namespace = namespace == null ? "default" : namespace;
    }

    public static DefaultHarnessIdentity of(String agentId) {
        return new DefaultHarnessIdentity(agentId, "default", "anonymous", null, "default");
    }

    public static DefaultHarnessIdentity of(String agentId, String tenantId, String userId) {
        return new DefaultHarnessIdentity(agentId, tenantId, userId, null, "default");
    }
}
