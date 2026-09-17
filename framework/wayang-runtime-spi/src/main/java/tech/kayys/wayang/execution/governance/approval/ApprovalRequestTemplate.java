package tech.kayys.wayang.execution.governance.approval;

import tech.kayys.wayang.tool.ToolInvocation;

import java.time.Duration;
import java.util.Map;

/**
 * Template used to create an {@link ApprovalRequest}.
 */
public record ApprovalRequestTemplate(
        ToolInvocation invocation,
        String tenantId,
        String userId,
        String agentId,
        String executionId,
        String correlationId,
        String reason,
        Duration ttl,
        Map<String, Object> metadata
) {

    public ApprovalRequestTemplate {
        if (invocation == null) {
            throw new IllegalArgumentException("invocation cannot be null");
        }

        reason = reason == null ? "" : reason.trim();
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);

        if (ttl != null && ttl.isNegative()) {
            throw new IllegalArgumentException("ttl cannot be negative");
        }
    }
}
