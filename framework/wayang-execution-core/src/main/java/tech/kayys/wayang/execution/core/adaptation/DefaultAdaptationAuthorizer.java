package tech.kayys.wayang.execution.core.adaptation;

import tech.kayys.wayang.execution.adaptation.AdaptationAuthorizer;
import tech.kayys.wayang.execution.adaptation.AdaptationPlan;
import tech.kayys.wayang.execution.adaptation.AuthorizationDecision;
import tech.kayys.wayang.execution.adaptation.RiskLevel;
import tech.kayys.wayang.execution.conformance.ConformanceProfile;
import tech.kayys.wayang.execution.mobility.ExecutionIdentity;

import java.util.Objects;

/**
 * Authorizer verifying that an adaptation plan does not violate declared execution semantics or policy.
 */
public class DefaultAdaptationAuthorizer implements AdaptationAuthorizer {

    private final String authorizerId;

    public DefaultAdaptationAuthorizer(String authorizerId) {
        this.authorizerId = Objects.requireNonNull(authorizerId, "authorizerId cannot be null");
    }

    public DefaultAdaptationAuthorizer() {
        this("wayang-adaptation-governor");
    }

    @Override
    public AuthorizationDecision authorize(AdaptationPlan plan, ExecutionIdentity executionId, ConformanceProfile declaredProfile) {
        Objects.requireNonNull(plan, "plan cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");

        // Rule 1: Risk level cannot be UNACCEPTABLE
        if (plan.risk().risk() == RiskLevel.UNACCEPTABLE) {
            return AuthorizationDecision.rejected(authorizerId, "Adaptation rejected: unacceptable risk level - " + plan.risk().rationale());
        }

        // Rule 2: Cannot alter semantics without explicit human approval if required
        if (plan.risk().requiresHumanApproval()) {
            return AuthorizationDecision.rejected(authorizerId, "Requires human approval before enacting: " + plan.risk().rationale());
        }

        return AuthorizationDecision.approved(authorizerId, "Adaptation plan conforms to execution contracts");
    }
}
