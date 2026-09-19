package tech.kayys.wayang.spi.operator.audit;

import tech.kayys.wayang.spi.operator.authorization.OperatorScope;

import java.time.Instant;
import java.util.Map;

/**
 * Structured, immutable operator audit event.
 */
public record OperatorAuditEvent(
        String id,
        Instant timestamp,
        String tenantId,
        String subjectId,
        String correlationId,
        String requestId,
        String operation,
        String permission,
        OperatorScope scope,
        String resourceType,
        String resourceId,
        boolean destructive,
        boolean privileged,
        OperatorAuditOutcome outcome,
        String authorizationCode,
        String message,
        Map<String, Object> before,
        Map<String, Object> after,
        Map<String, Object> attributes
) {
    public OperatorAuditEvent {
        timestamp = timestamp != null ? timestamp : Instant.now();
        before = before != null ? Map.copyOf(before) : Map.of();
        after = after != null ? Map.copyOf(after) : Map.of();
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }
}
