package tech.kayys.andalus.execution.adaptation;

import java.util.Objects;

/**
 * Formal governance decision authorizing or rejecting an adaptation plan.
 */
public record AuthorizationDecision(
    boolean authorized,
    String reason,
    String authorizerId
) {
    public AuthorizationDecision {
        Objects.requireNonNull(reason, "reason cannot be null");
        Objects.requireNonNull(authorizerId, "authorizerId cannot be null");
    }

    public static AuthorizationDecision approved(String authorizerId, String reason) {
        return new AuthorizationDecision(true, reason, authorizerId);
    }

    public static AuthorizationDecision rejected(String authorizerId, String reason) {
        return new AuthorizationDecision(false, reason, authorizerId);
    }
}
