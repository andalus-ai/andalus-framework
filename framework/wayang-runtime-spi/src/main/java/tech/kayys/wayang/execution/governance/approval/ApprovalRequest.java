package tech.kayys.wayang.execution.governance.approval;

import tech.kayys.wayang.tool.ToolInvocation;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable stateful approval request for a tool invocation.
 */
public record ApprovalRequest(
        String id,
        ToolInvocation invocation,
        String tenantId,
        String userId,
        String agentId,
        String executionId,
        String correlationId,
        String reason,
        Instant createdAt,
        Instant expiresAt,
        ApprovalStatus status,
        String decidedBy,
        Instant decidedAt,
        String decisionReason,
        Map<String, Object> metadata
) {

    public ApprovalRequest {
        id = id == null || id.isBlank() ? UUID.randomUUID().toString() : id.trim();
        Objects.requireNonNull(invocation, "invocation cannot be null");

        tenantId = normalize(tenantId);
        userId = normalize(userId);
        agentId = normalize(agentId);
        executionId = normalize(executionId);
        correlationId = normalize(correlationId);

        reason = reason == null ? "" : reason.trim();
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
        Objects.requireNonNull(status, "status cannot be null");

        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);

        if (expiresAt != null && expiresAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("expiresAt cannot be before createdAt");
        }

        decidedBy = normalize(decidedBy);
        decisionReason = normalize(decisionReason);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public boolean pending() {
        return status == ApprovalStatus.PENDING;
    }

    public boolean terminal() {
        return status != ApprovalStatus.PENDING;
    }

    public boolean expired(Instant now) {
        Objects.requireNonNull(now, "now cannot be null");
        return expiresAt != null && !now.isBefore(expiresAt);
    }
}
