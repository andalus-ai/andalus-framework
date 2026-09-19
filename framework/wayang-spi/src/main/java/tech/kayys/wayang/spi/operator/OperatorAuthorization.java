package tech.kayys.wayang.spi.operator;

/**
 * Authorization guard interface for operator actions.
 */
public interface OperatorAuthorization {

    /**
     * Require that the operator context has the specified permission.
     *
     * @param context the operator context
     * @param permission the required permission
     * @throws SecurityException if unauthorized
     */
    void require(OperatorContext context, OperatorPermission permission);
}
