package tech.kayys.wayang.execution.governance.audit;

import tech.kayys.wayang.execution.governance.PolicyEvaluationContext;

import java.util.Map;

/**
 * Factory for common security event lifecycle instances.
 */
public final class SecurityEvents {

    private SecurityEvents() {
    }

    public static SecurityEvent invocationStarted(
            PolicyEvaluationContext context,
            String providerId) {

        return new SecurityEvent(
                null,
                SecurityEventType.TOOL_INVOCATION_STARTED,
                null,
                context.tenantId(),
                context.userId(),
                context.agentId(),
                context.executionId(),
                context.correlationId(),
                context.invocation().name(),
                providerId,
                null,
                null,
                "STARTED",
                null,
                context.resources(),
                Map.of()
        );
    }

    public static SecurityEvent invocationDenied(
            PolicyEvaluationContext context,
            String policyId,
            String reason) {

        return new SecurityEvent(
                null,
                SecurityEventType.TOOL_INVOCATION_DENIED,
                null,
                context.tenantId(),
                context.userId(),
                context.agentId(),
                context.executionId(),
                context.correlationId(),
                context.invocation().name(),
                null,
                policyId,
                null,
                "DENIED",
                reason,
                context.resources(),
                Map.of()
        );
    }

    public static SecurityEvent approvalRequired(
            PolicyEvaluationContext context,
            String approvalId,
            String reason) {

        return new SecurityEvent(
                null,
                SecurityEventType.TOOL_APPROVAL_REQUIRED,
                null,
                context.tenantId(),
                context.userId(),
                context.agentId(),
                context.executionId(),
                context.correlationId(),
                context.invocation().name(),
                null,
                null,
                approvalId,
                "PENDING",
                reason,
                context.resources(),
                Map.of()
        );
    }

    public static SecurityEvent invocationAllowed(
            PolicyEvaluationContext context,
            String providerId) {

        return new SecurityEvent(
                null,
                SecurityEventType.TOOL_INVOCATION_ALLOWED,
                null,
                context.tenantId(),
                context.userId(),
                context.agentId(),
                context.executionId(),
                context.correlationId(),
                context.invocation().name(),
                providerId,
                null,
                null,
                "ALLOWED",
                null,
                context.resources(),
                Map.of()
        );
    }

    public static SecurityEvent invocationFailed(
            PolicyEvaluationContext context,
            String providerId,
            String reason) {

        return new SecurityEvent(
                null,
                SecurityEventType.TOOL_INVOCATION_FAILED,
                null,
                context.tenantId(),
                context.userId(),
                context.agentId(),
                context.executionId(),
                context.correlationId(),
                context.invocation().name(),
                providerId,
                null,
                null,
                "FAILED",
                reason,
                context.resources(),
                Map.of()
        );
    }
}
