package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.execution.governance.approval.ApprovalBindingValidator;
import tech.kayys.wayang.execution.governance.approval.ApprovalRequest;
import tech.kayys.wayang.execution.governance.approval.ApprovalRequestTemplate;
import tech.kayys.wayang.execution.governance.approval.ApprovalService;
import tech.kayys.wayang.execution.governance.approval.ToolApprovalRequiredException;
import tech.kayys.wayang.tool.ToolContext;
import tech.kayys.wayang.tool.ToolExecutor;
import tech.kayys.wayang.tool.ToolInvocation;
import tech.kayys.wayang.tool.ToolResult;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Standard implementation of {@link ToolExecutionGuard} enforcing policies, authorizations, and approvals.
 */
public final class DefaultToolExecutionGuard implements ToolExecutionGuard {

    private final PolicyEvaluationContextFactory contextFactory;
    private final ToolPolicyEvaluator policyEvaluator;
    private final ApprovalService approvalService;
    private final ApprovalBindingValidator approvalBindingValidator;

    public DefaultToolExecutionGuard(
            PolicyEvaluationContextFactory contextFactory,
            ToolPolicyEvaluator policyEvaluator,
            ApprovalService approvalService,
            ApprovalBindingValidator approvalBindingValidator) {

        this.contextFactory = Objects.requireNonNull(contextFactory, "contextFactory cannot be null");
        this.policyEvaluator = Objects.requireNonNull(policyEvaluator, "policyEvaluator cannot be null");
        this.approvalService = Objects.requireNonNull(approvalService, "approvalService cannot be null");
        this.approvalBindingValidator = Objects.requireNonNull(approvalBindingValidator, "approvalBindingValidator cannot be null");
    }

    @Override
    public CompletableFuture<ToolResult> execute(
            ToolInvocation invocation,
            ToolContext toolContext,
            ToolExecutor delegate) {

        Objects.requireNonNull(invocation, "invocation cannot be null");
        Objects.requireNonNull(toolContext, "toolContext cannot be null");
        Objects.requireNonNull(delegate, "delegate cannot be null");

        PolicyEvaluationContext context = contextFactory.create(invocation, toolContext);
        PolicyDecision decision = policyEvaluator.evaluate(context);

        if (decision instanceof PolicyDecision.Deny || decision.isDenied()) {
            String msg = decision.message() != null ? decision.message() : "Tool execution denied";
            return CompletableFuture.failedFuture(new ToolExecutionDeniedException(msg));
        }

        if (decision instanceof PolicyDecision.RequireApproval || decision.requiresApproval()) {
            ApprovalRequestTemplate template = new ApprovalRequestTemplate(
                    invocation,
                    context.tenantId(),
                    context.userId(),
                    context.agentId(),
                    context.executionId(),
                    context.correlationId(),
                    decision.message(),
                    null,
                    context.attributes()
            );

            ApprovalRequest request = approvalService.request(template);
            return CompletableFuture.failedFuture(new ToolApprovalRequiredException(request.id()));
        }

        return delegate.execute(invocation, toolContext);
    }

    @Override
    public CompletableFuture<ToolResult> executeApproved(
            String approvalId,
            ToolInvocation invocation,
            ToolContext toolContext,
            ToolExecutor delegate) {

        Objects.requireNonNull(approvalId, "approvalId cannot be null");
        Objects.requireNonNull(invocation, "invocation cannot be null");
        Objects.requireNonNull(toolContext, "toolContext cannot be null");
        Objects.requireNonNull(delegate, "delegate cannot be null");

        PolicyEvaluationContext context = contextFactory.create(invocation, toolContext);
        ApprovalRequest approval = approvalService.get(approvalId);

        approvalBindingValidator.validate(approval, invocation, context);

        // Re-evaluate policy to ensure no changes made it denied
        PolicyDecision decision = policyEvaluator.evaluate(context);
        if (decision instanceof PolicyDecision.Deny || decision.isDenied()) {
            String msg = decision.message() != null ? decision.message() : "Tool execution denied by policy";
            return CompletableFuture.failedFuture(new ToolExecutionDeniedException(msg));
        }

        return delegate.execute(invocation, toolContext);
    }
}
