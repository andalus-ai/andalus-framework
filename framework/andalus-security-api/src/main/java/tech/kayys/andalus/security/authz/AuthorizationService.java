package tech.kayys.andalus.security.authz;

import java.util.concurrent.CompletionStage;

/**
 * SPI for authorization.
 * Connects to RBAC, ABAC, tenant-policy, and capability-policy engines
 * without coupling Andalus core to any specific implementation.
 */
public interface AuthorizationService {

    CompletionStage<AuthorizationDecision> authorize(AuthorizationRequest request);
}
