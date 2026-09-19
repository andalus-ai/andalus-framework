package tech.kayys.wayang.spi.operator.authorization;

/**
 * Result of an operator authorization check.
 */
public record OperatorAuthorizationResult(
        OperatorAuthorizationDecision decision,
        String code,
        String message,
        OperatorScope scope
) {

    public static OperatorAuthorizationResult allow(
            OperatorScope scope
    ) {
        return new OperatorAuthorizationResult(
                OperatorAuthorizationDecision.ALLOW,
                "AUTHORIZED",
                "Operator operation authorized",
                scope
        );
    }

    public static OperatorAuthorizationResult deny(
            String code,
            String message
    ) {
        return new OperatorAuthorizationResult(
                OperatorAuthorizationDecision.DENY,
                code,
                message,
                null
        );
    }

    public boolean allowed() {
        return decision == OperatorAuthorizationDecision.ALLOW;
    }
}
