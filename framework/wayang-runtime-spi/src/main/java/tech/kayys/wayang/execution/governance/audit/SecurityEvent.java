package tech.kayys.wayang.execution.governance.audit;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical immutable security event for governance and audit trails.
 */
public record SecurityEvent(
        String eventId,
        SecurityEventType type,
        Instant timestamp,
        String tenantId,
        String userId,
        String agentId,
        String executionId,
        String correlationId,
        String toolName,
        String providerId,
        String policyId,
        String approvalId,
        String outcome,
        String reason,
        Map<String, String> resources,
        Map<String, Object> attributes
) {

    public SecurityEvent {
        eventId = eventId == null || eventId.isBlank()
                ? UUID.randomUUID().toString()
                : eventId.trim();

        Objects.requireNonNull(
                type,
                "type cannot be null"
        );

        timestamp = timestamp == null
                ? Instant.now()
                : timestamp;

        tenantId = normalize(tenantId);
        userId = normalize(userId);
        agentId = normalize(agentId);
        executionId = normalize(executionId);
        correlationId = normalize(correlationId);
        toolName = normalize(toolName);
        providerId = normalize(providerId);
        policyId = normalize(policyId);
        approvalId = normalize(approvalId);
        outcome = normalize(outcome);
        reason = normalize(reason);

        resources = resources == null
                ? Map.of()
                : Map.copyOf(resources);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
