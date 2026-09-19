package tech.kayys.wayang.spi.operator;

/**
 * Exception thrown when an operator operation fails authorization.
 */
public class OperatorAuthorizationException extends RuntimeException {

    private final String code;

    public OperatorAuthorizationException(String code, String message) {
        super(message);
        this.code = code != null ? code : "OPERATOR_AUTHORIZATION_DENIED";
    }

    public String code() {
        return code;
    }
}
