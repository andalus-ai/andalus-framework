package tech.kayys.andalus.spi.operator.authorization;

/**
 * Checks resource-level ownership and tenant boundaries.
 */
public interface OperatorResourceAuthorizer {

    boolean canAccess(OperatorAuthorizationRequest request);
}
