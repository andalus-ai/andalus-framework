package tech.kayys.wayang.execution.adaptation;

import tech.kayys.wayang.execution.conformance.ConformanceProfile;
import tech.kayys.wayang.execution.mobility.ExecutionIdentity;

/**
 * Authorizer verifying that an adaptation plan does not violate declared contracts or policies.
 */
public interface AdaptationAuthorizer {

    AuthorizationDecision authorize(AdaptationPlan plan, ExecutionIdentity executionId, ConformanceProfile declaredProfile);
}
