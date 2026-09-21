package tech.kayys.andalus.execution.adaptation;

import tech.kayys.andalus.execution.conformance.ConformanceProfile;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;

/**
 * Authorizer verifying that an adaptation plan does not violate declared contracts or policies.
 */
public interface AdaptationAuthorizer {

    AuthorizationDecision authorize(AdaptationPlan plan, ExecutionIdentity executionId, ConformanceProfile declaredProfile);
}
