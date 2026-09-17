package tech.kayys.wayang.execution.governance.approval;

import tech.kayys.wayang.execution.governance.PolicyEvaluationContext;
import tech.kayys.wayang.tool.ToolInvocation;

/**
 * Validates that an {@link ApprovalRequest} matches the current invocation and execution context.
 */
public interface ApprovalBindingValidator {

    void validate(
            ApprovalRequest approval,
            ToolInvocation invocation,
            PolicyEvaluationContext context);
}
