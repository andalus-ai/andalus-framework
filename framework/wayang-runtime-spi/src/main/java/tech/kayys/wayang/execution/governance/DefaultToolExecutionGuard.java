package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.execution.governance.approval.ApprovalBindingValidator;
import tech.kayys.wayang.execution.governance.approval.ApprovalRequest;
import tech.kayys.wayang.execution.governance.approval.ApprovalRequestTemplate;
import tech.kayys.wayang.execution.governance.approval.ApprovalService;
import tech.kayys.wayang.execution.governance.approval.ToolApprovalRequiredException;
import tech.kayys.wayang.execution.governance.audit.ToolAuditEvent;
import tech.kayys.wayang.execution.governance.audit.ToolAuditEventType;
import tech.kayys.wayang.execution.governance.audit.ToolAuditPublisher;
import tech.kayys.wayang.execution.governance.quota.QuotaExceededException;
import tech.kayys.wayang.execution.governance.quota.QuotaManager;
import tech.kayys.wayang.tool.ToolContext;
import tech.kayys.wayang.tool.ToolExecutor;
import tech.kayys.wayang.tool.ToolInvocation;
import tech.kayys.wayang.tool.ToolResult;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Standard implementation of {@link ToolExecutionGuard} enforcing policies, authorizations,
 * approvals, quota/rate limits, and publishing audit & security events.
 */
public final class DefaultToolExecutionGuard implements ToolExecutionGuard {

    private final PolicyEvaluationContextFactory contextFactory;
    private final ToolPolicyEvaluator policyEvaluator;
    private final ApprovalService approvalService;
    private final ApprovalBindingValidator approvalBindingValidator;
    private final ToolAuditPublisher auditPublisher;
    private final QuotaManager quotaManager;

    public DefaultToolExecutionGuard(
            PolicyEvaluationContextFactory contextFactory,
            ToolPolicyEvaluator policyEvaluator,
            ApprovalService approvalService,
            ApprovalBindingValidator approvalBindingValidator) {
        this(contextFactory, policyEvaluator, approvalService, approvalBindingValidator, ToolAuditPublisher.noop(), QuotaManager.noop());
    }

    public DefaultToolExecutionGuard(
            PolicyEvaluationContextFactory contextFactory,
            ToolPolicyEvaluator policyEvaluator,
            ApprovalService approvalService,
            ApprovalBindingValidator approvalBindingValidator,
            ToolAuditPublisher auditPublisher) {
        this(contextFactory, policyEvaluator, approvalService, approvalBindingValidator, auditPublisher, QuotaManager.noop());
    }

    public DefaultToolExecutionGuard(
            PolicyEvaluationContextFactory contextFactory,
            ToolPolicyEvaluator policyEvaluator,
            ApprovalService approvalService,
            ApprovalBindingValidator approvalBindingValidator,
            ToolAuditPublisher auditPublisher,
            QuotaManager quotaManager) {

        this.contextFactory = Objects.requireNonNull(contextFactory, "contextFactory cannot be null");
        this.policyEvaluator = Objects.requireNonNull(policyEvaluator, "policyEvaluator cannot be null");
        this.approvalService = Objects.requireNonNull(approvalService, "approvalService cannot be null");
        this.approvalBindingValidator = Objects.requireNonNull(approvalBindingValidator, "approvalBindingValidator cannot be null");
        this.auditPublisher = Objects.requireNonNull(auditPublisher, "auditPublisher cannot be null");
        this.quotaManager = Objects.requireNonNull(quotaManager, "quotaManager cannot be null");
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

        // 1. Quota & Rate Limit Check
        try {
            quotaManager.acquirePermit(invocation, context);
        } catch (QuotaExceededException qe) {
            ToolAuditEventType eventType = "RATE_LIMIT_EXCEEDED".equals(qe.reason())
                    ? ToolAuditEventType.RATE_LIMIT_EXCEEDED
                    : ToolAuditEventType.BUDGET_EXCEEDED;

            auditPublisher.publish(ToolAuditEvent.builder(eventType, invocation.name())
                    .tenantId(context.tenantId())
                    .userId(context.userId())
                    .agentId(context.agentId())
                    .executionId(context.executionId())
                    .correlationId(context.correlationId())
                    .decision("DENY")
                    .reason(qe.getMessage())
                    .build());

            return CompletableFuture.failedFuture(qe);
        }

        // 2. Policy Evaluation
        PolicyEvaluationResult evalResult = policyEvaluator.evaluateDetailed(invocation, context.permissionContext());
        PolicyDecision decision = evalResult.decision();

        String decisionStr = decision.isAllowed() ? "ALLOW" : (decision.isDenied() ? "DENY" : "REQUIRE_APPROVAL");

        auditPublisher.publish(ToolAuditEvent.builder(ToolAuditEventType.POLICY_EVALUATED, invocation.name())
                .tenantId(context.tenantId())
                .userId(context.userId())
                .agentId(context.agentId())
                .executionId(context.executionId())
                .correlationId(context.correlationId())
                .decision(decisionStr)
                .reason(decision.message())
                .build());

        if (decision instanceof PolicyDecision.Deny || decision.isDenied()) {
            String msg = decision.message() != null ? decision.message() : "Tool execution denied";
            auditPublisher.publish(ToolAuditEvent.builder(ToolAuditEventType.EXECUTION_DENIED, invocation.name())
                    .tenantId(context.tenantId())
                    .userId(context.userId())
                    .agentId(context.agentId())
                    .executionId(context.executionId())
                    .correlationId(context.correlationId())
                    .decision("DENY")
                    .reason(msg)
                    .build());
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

            auditPublisher.publish(ToolAuditEvent.builder(ToolAuditEventType.APPROVAL_REQUESTED, invocation.name())
                    .tenantId(context.tenantId())
                    .userId(context.userId())
                    .agentId(context.agentId())
                    .executionId(context.executionId())
                    .correlationId(context.correlationId())
                    .decision("REQUIRE_APPROVAL")
                    .reason(decision.message())
                    .build());

            return CompletableFuture.failedFuture(new ToolApprovalRequiredException(request.id()));
        }

        auditPublisher.publish(ToolAuditEvent.builder(ToolAuditEventType.EXECUTION_ALLOWED, invocation.name())
                .tenantId(context.tenantId())
                .userId(context.userId())
                .agentId(context.agentId())
                .executionId(context.executionId())
                .correlationId(context.correlationId())
                .decision("ALLOW")
                .build());

        return executeWithAuditAndQuota(invocation, toolContext, delegate, context);
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
            auditPublisher.publish(ToolAuditEvent.builder(ToolAuditEventType.EXECUTION_DENIED, invocation.name())
                    .tenantId(context.tenantId())
                    .userId(context.userId())
                    .agentId(context.agentId())
                    .executionId(context.executionId())
                    .correlationId(context.correlationId())
                    .decision("DENY")
                    .reason(msg)
                    .build());
            return CompletableFuture.failedFuture(new ToolExecutionDeniedException(msg));
        }

        auditPublisher.publish(ToolAuditEvent.builder(ToolAuditEventType.APPROVAL_GRANTED, invocation.name())
                .tenantId(context.tenantId())
                .userId(context.userId())
                .agentId(context.agentId())
                .executionId(context.executionId())
                .correlationId(context.correlationId())
                .decision("ALLOW")
                .approverId(approval.decidedBy())
                .reason(approval.decisionReason())
                .build());

        return executeWithAuditAndQuota(invocation, toolContext, delegate, context);
    }

    private CompletableFuture<ToolResult> executeWithAuditAndQuota(
            ToolInvocation invocation,
            ToolContext toolContext,
            ToolExecutor delegate,
            PolicyEvaluationContext context) {

        long startNs = System.nanoTime();
        auditPublisher.publish(ToolAuditEvent.builder(ToolAuditEventType.EXECUTION_STARTED, invocation.name())
                .tenantId(context.tenantId())
                .userId(context.userId())
                .agentId(context.agentId())
                .executionId(context.executionId())
                .correlationId(context.correlationId())
                .build());

        return delegate.execute(invocation, toolContext).whenComplete((result, ex) -> {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000;
            double cost = 0.0;
            if (toolContext.attributes().get("costUsd") instanceof Number n) {
                cost = n.doubleValue();
            }

            quotaManager.recordConsumption(invocation, context, durationMs, cost);

            if (ex != null || (result != null && !result.isSuccess())) {
                String errorMsg = ex != null ? ex.getMessage() : (result != null ? result.getErrorMessage() : "Execution failed");
                auditPublisher.publish(ToolAuditEvent.builder(ToolAuditEventType.EXECUTION_FAILED, invocation.name())
                        .tenantId(context.tenantId())
                        .userId(context.userId())
                        .agentId(context.agentId())
                        .executionId(context.executionId())
                        .correlationId(context.correlationId())
                        .durationMs(durationMs)
                        .reason(errorMsg)
                        .build());
            } else {
                auditPublisher.publish(ToolAuditEvent.builder(ToolAuditEventType.EXECUTION_COMPLETED, invocation.name())
                        .tenantId(context.tenantId())
                        .userId(context.userId())
                        .agentId(context.agentId())
                        .executionId(context.executionId())
                        .correlationId(context.correlationId())
                        .durationMs(durationMs)
                        .build());
            }
        });
    }
}
