package tech.kayys.andalus.harness.security.authorization;

/**
 * Universal authorization engine contract evaluating access permissions under policy.
 */
public interface AuthorizationEngine {

    AuthorizationDecision authorize(AuthorizationRequest request);
}
