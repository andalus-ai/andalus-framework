package tech.kayys.wayang.execution.governance.audit;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable audit and security event record for tool governance and execution lifecycle.
 * <p>
 * Ensures tenant isolation, correlation tracking, and policy transparency without
 * leaking sensitive payloads or secrets.
 *
 * @param eventId       Unique ID for this event.
 * @param eventType     The canonical governance event type.
 * @param timestamp     When the event occurred.
 * @param toolName      The name of the tool invoked or evaluated.
 * @param providerId    The provider ID handling the tool, if known.
 * @param tenantId      Owning tenant ID for tenant isolation.
 * @param userId        The invoking user ID, if known.
 * @param agentId       The invoking agent ID, if known.
 * @param executionId   Execution identifier.
 * @param correlationId Correlation identifier for tracing chained requests.
 * @param decision      The policy decision effect (e.g. ALLOW, DENY, REQUIRE_APPROVAL).
 * @param policyId      The ID of the policy that dictated the outcome, if applicable.
 * @param reason        The reason for a decision (denial reason, policy message).
 * @param approverId    The user/principal who approved or rejected, if applicable.
 * @param durationMs    Duration of execution in milliseconds, if applicable.
 * @param attributes    Safe, redacted attributes (no sensitive payload/secrets).
 */
public record ToolAuditEvent(
        String eventId,
        ToolAuditEventType eventType,
        Instant timestamp,
        String toolName,
        String providerId,
        String tenantId,
        String userId,
        String agentId,
        String executionId,
        String correlationId,
        String decision,
        String policyId,
        String reason,
        String approverId,
        Long durationMs,
        Map<String, Object> attributes
) {

    public ToolAuditEvent {
        eventId = (eventId == null || eventId.isBlank()) ? UUID.randomUUID().toString() : eventId;
        Objects.requireNonNull(eventType, "eventType cannot be null");
        timestamp = timestamp == null ? Instant.now() : timestamp;
        toolName = Objects.requireNonNull(toolName, "toolName cannot be null");

        providerId = normalize(providerId);
        tenantId = normalize(tenantId);
        userId = normalize(userId);
        agentId = normalize(agentId);
        executionId = normalize(executionId);
        correlationId = normalize(correlationId);
        decision = normalize(decision);
        policyId = normalize(policyId);
        reason = normalize(reason);
        approverId = normalize(approverId);

        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    private static String normalize(String val) {
        return (val == null || val.isBlank()) ? null : val.trim();
    }

    public static Builder builder(ToolAuditEventType eventType, String toolName) {
        return new Builder(eventType, toolName);
    }

    public static final class Builder {
        private String eventId;
        private final ToolAuditEventType eventType;
        private Instant timestamp;
        private final String toolName;
        private String providerId;
        private String tenantId;
        private String userId;
        private String agentId;
        private String executionId;
        private String correlationId;
        private String decision;
        private String policyId;
        private String reason;
        private String approverId;
        private Long durationMs;
        private Map<String, Object> attributes;

        private Builder(ToolAuditEventType eventType, String toolName) {
            this.eventType = eventType;
            this.toolName = toolName;
        }

        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public Builder providerId(String providerId) { this.providerId = providerId; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder agentId(String agentId) { this.agentId = agentId; return this; }
        public Builder executionId(String executionId) { this.executionId = executionId; return this; }
        public Builder correlationId(String correlationId) { this.correlationId = correlationId; return this; }
        public Builder decision(String decision) { this.decision = decision; return this; }
        public Builder policyId(String policyId) { this.policyId = policyId; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder approverId(String approverId) { this.approverId = approverId; return this; }
        public Builder durationMs(Long durationMs) { this.durationMs = durationMs; return this; }
        public Builder attributes(Map<String, Object> attributes) { this.attributes = attributes; return this; }

        public ToolAuditEvent build() {
            return new ToolAuditEvent(
                    eventId, eventType, timestamp, toolName, providerId,
                    tenantId, userId, agentId, executionId, correlationId,
                    decision, policyId, reason, approverId, durationMs, attributes
            );
        }
    }
}
