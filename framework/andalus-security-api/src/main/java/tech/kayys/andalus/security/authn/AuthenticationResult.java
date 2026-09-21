package tech.kayys.andalus.security.authn;

import tech.kayys.andalus.security.context.SecurityContext;

/**
 * Result of an authentication attempt.
 */
public record AuthenticationResult(
        boolean authenticated,
        SecurityContext context,
        String errorCode,
        String errorMessage
) {

    public static AuthenticationResult success(SecurityContext context) {
        return new AuthenticationResult(true, context, null, null);
    }

    public static AuthenticationResult failure(String code, String message) {
        return new AuthenticationResult(false, null, code, message);
    }
}
