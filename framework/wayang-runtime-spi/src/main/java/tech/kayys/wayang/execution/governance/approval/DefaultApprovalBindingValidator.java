package tech.kayys.wayang.execution.governance.approval;

import tech.kayys.wayang.execution.governance.PolicyEvaluationContext;
import tech.kayys.wayang.tool.ToolInvocation;

import java.time.Clock;
import java.util.Objects;

/**
 * Standard implementation of {@link ApprovalBindingValidator}.
 */
public final class DefaultApprovalBindingValidator implements ApprovalBindingValidator {

    private final Clock clock;

    public DefaultApprovalBindingValidator(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    public DefaultApprovalBindingValidator() {
        this(Clock.systemUTC());
    }

    @Override
    public void validate(
            ApprovalRequest approval,
            ToolInvocation invocation,
            PolicyEvaluationContext context) {

        Objects.requireNonNull(approval, "approval cannot be null");
        Objects.requireNonNull(invocation, "invocation cannot be null");
        Objects.requireNonNull(context, "context cannot be null");

        if (approval.status() != ApprovalStatus.APPROVED) {
            throw new ApprovalBindingException("Approval is not approved: " + approval.id() + " (status=" + approval.status() + ")");
        }

        if (!Objects.equals(approval.invocation().name(), invocation.name())) {
            throw new ApprovalBindingException("Approval tool (" + approval.invocation().name() + ") does not match invocation (" + invocation.name() + ")");
        }

        if (!Objects.equals(approval.tenantId(), context.tenantId())) {
            throw new ApprovalBindingException("Approval tenant (" + approval.tenantId() + ") does not match invocation (" + context.tenantId() + ")");
        }

        if (approval.executionId() != null && !Objects.equals(approval.executionId(), context.executionId())) {
            throw new ApprovalBindingException("Approval executionId (" + approval.executionId() + ") does not match invocation (" + context.executionId() + ")");
        }

        if (approval.agentId() != null && !Objects.equals(approval.agentId(), context.agentId())) {
            throw new ApprovalBindingException("Approval agentId (" + approval.agentId() + ") does not match invocation (" + context.agentId() + ")");
        }

        if (approval.expired(clock.instant())) {
            throw new ApprovalBindingException("Approval has expired: " + approval.id());
        }
    }
}
