package tech.kayys.andalus.harness.decision.governance;

import tech.kayys.andalus.harness.decision.trace.DecisionTraceAppendRequest;
import tech.kayys.andalus.harness.decision.trace.DecisionTraceEntry;
import tech.kayys.andalus.harness.decision.trace.DecisionTraceEntryType;
import tech.kayys.andalus.harness.decision.trace.DecisionTraceRuntime;
import tech.kayys.andalus.harness.protocol.AgentDecision;
import tech.kayys.andalus.harness.protocol.AgentDecisionType;
import tech.kayys.andalus.harness.protocol.DecisionValidationResult;
import tech.kayys.andalus.harness.protocol.DecisionValidator;
import tech.kayys.andalus.harness.protocol.DefaultDecisionValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Default implementation of {@link DecisionGovernance} coordinating structural validation,
 * constraint checks, human oversight (HITL), and trace recording.
 */
public class DefaultDecisionGovernance implements DecisionGovernance {

    private final DecisionValidator decisionValidator;
    private final DecisionTraceRuntime traceRuntime;

    public DefaultDecisionGovernance() {
        this(new DefaultDecisionValidator(), null);
    }

    public DefaultDecisionGovernance(DecisionValidator decisionValidator, DecisionTraceRuntime traceRuntime) {
        this.decisionValidator = decisionValidator != null ? decisionValidator : new DefaultDecisionValidator();
        this.traceRuntime = traceRuntime;
    }

    @Override
    public CompletionStage<DecisionGovernanceResult> evaluate(DecisionGovernanceRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        try {
            AgentDecision decision = request.decision();
            List<GovernanceFinding> findings = new ArrayList<>();
            List<GovernanceObligation> obligations = new ArrayList<>();

            // 1. Structural validation
            DecisionValidationResult validation = decisionValidator.validate(decision, request.context());
            if (!validation.valid()) {
                String errorReason = (validation.reason() != null && !validation.reason().isBlank())
                        ? validation.reason()
                        : "Structural validation failed";
                findings.add(GovernanceFinding.blocking(
                        "STRUCTURAL_VALIDATION_FAILED",
                        "decision-validator",
                        errorReason
                ));
                DecisionGovernanceResult result = DecisionGovernanceResult.denied(errorReason, findings);
                return recordTraceAndReturn(request, result);
            }

            findings.add(GovernanceFinding.info("STRUCTURAL_VALIDATION_PASSED", "decision-validator", "Decision is structurally valid"));

            // 2. Budget and resource execution constraints
            if (Boolean.TRUE.equals(request.attributes().get("budgetExceeded"))) {
                findings.add(GovernanceFinding.blocking("BUDGET_EXCEEDED", "execution-constraints", "Execution budget exceeded"));
                DecisionGovernanceResult result = DecisionGovernanceResult.denied("Execution budget exceeded", findings);
                return recordTraceAndReturn(request, result);
            }

            // 3. Prohibited provider / policy restrictions
            if (Boolean.TRUE.equals(request.attributes().get("prohibitedProvider"))) {
                findings.add(GovernanceFinding.blocking("PROHIBITED_PROVIDER", "policy", "Target provider is prohibited by policy"));
                DecisionGovernanceResult result = DecisionGovernanceResult.denied("Provider prohibited by policy", findings);
                return recordTraceAndReturn(request, result);
            }

            // 4. Delegation constraints and authority attenuation
            if (decision.type() == AgentDecisionType.DELEGATE) {
                if (Boolean.TRUE.equals(request.attributes().get("delegationAuthorityExceeded"))) {
                    findings.add(GovernanceFinding.blocking(
                            "DELEGATION_AUTHORITY_EXCEEDED",
                            "delegation-guard",
                            "Delegated authority cannot exceed parent authority"
                    ));
                    DecisionGovernanceResult result = DecisionGovernanceResult.denied("Delegation authority exceeded", findings);
                    return recordTraceAndReturn(request, result);
                }
            }

            // 5. Human Oversight / HITL check
            boolean requiresApproval = Boolean.TRUE.equals(request.attributes().get("requiresApproval"))
                    || Boolean.TRUE.equals(request.attributes().get("requiresHitl"));
            boolean humanApproved = Boolean.TRUE.equals(request.attributes().get("humanApproved"));

            if (decision.type() == AgentDecisionType.ASK_HUMAN) {
                findings.add(GovernanceFinding.info("HITL_REQUESTED", "decision", "Agent proposed asking human for input"));
                obligations.add(GovernanceObligation.hitlApproval("user", "Decision requests human interaction"));
                DecisionGovernanceResult result = DecisionGovernanceResult.waitRequired("Waiting for human response", findings, obligations);
                return recordTraceAndReturn(request, result);
            }

            if (requiresApproval && !humanApproved) {
                findings.add(GovernanceFinding.warning("APPROVAL_REQUIRED", "governance-policy", "Action requires human approval before proceeding"));
                obligations.add(GovernanceObligation.hitlApproval("operator", "Operation requires human approval"));
                DecisionGovernanceResult result = DecisionGovernanceResult.waitRequired("Requires human approval", findings, obligations);
                return recordTraceAndReturn(request, result);
            }

            if (requiresApproval && humanApproved) {
                // Revalidation check
                if (Boolean.TRUE.equals(request.attributes().get("approvalInvalidated"))) {
                    findings.add(GovernanceFinding.blocking("APPROVAL_INVALIDATED", "governance", "Context changed since approval; revalidation failed"));
                    DecisionGovernanceResult result = DecisionGovernanceResult.denied("Approval invalidated due to context change", findings);
                    return recordTraceAndReturn(request, result);
                }
                findings.add(GovernanceFinding.info("HITL_APPROVED", "governance", "Human approval verified and active"));
            }

            // 6. All checks passed -> ALLOW
            findings.add(GovernanceFinding.info("GOVERNANCE_PASSED", "governance", "Decision meets all governance requirements"));
            DecisionGovernanceResult allowedResult = DecisionGovernanceResult.allowed("Decision allowed under current governance constraints", findings);
            return recordTraceAndReturn(request, allowedResult);

        } catch (Exception ex) {
            // Fail closed on unhandled exceptions
            DecisionGovernanceResult errorResult = DecisionGovernanceResult.error("Governance evaluation encountered error", ex);
            return CompletableFuture.completedFuture(errorResult);
        }
    }

    private CompletionStage<DecisionGovernanceResult> recordTraceAndReturn(
            DecisionGovernanceRequest request,
            DecisionGovernanceResult result
    ) {
        if (traceRuntime == null || request.decisionTraceId().isEmpty()) {
            return CompletableFuture.completedFuture(result);
        }

        String traceId = request.decisionTraceId().get();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("governanceOutcome", result.outcome().name());
        result.reason().ifPresent(r -> attributes.put("governanceReason", r));
        attributes.put("findingsCount", result.findings().size());

        DecisionTraceEntryType entryType = (result.outcome() == GovernanceOutcome.ALLOW)
                ? DecisionTraceEntryType.DECISION_VALIDATED
                : DecisionTraceEntryType.CONSTRAINT_EVALUATED;

        DecisionTraceEntry entry = DecisionTraceEntry.of(
                "gov-" + UUID.randomUUID(),
                entryType,
                attributes
        );

        return traceRuntime.append(new DecisionTraceAppendRequest(traceId, entry))
                .handle((trace, ex) -> result); // Trace failure does not alter governance outcome
    }
}
