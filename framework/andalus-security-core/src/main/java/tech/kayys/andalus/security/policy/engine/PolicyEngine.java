package tech.kayys.andalus.security.policy.engine;

import tech.kayys.andalus.security.authz.AuthorizationRequest;
import tech.kayys.andalus.security.policy.PolicyDecision;

/**
 * Evaluates an {@link AuthorizationRequest} against registered policies and returns a {@link PolicyDecision}.
 */
public interface PolicyEngine {

    PolicyDecision evaluate(AuthorizationRequest request);
}
