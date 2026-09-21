package tech.kayys.andalus.spi.operator.authorization;

/**
 * SPI policy for evaluating operator authorization requests.
 */
public interface OperatorAuthorizationPolicy {

    OperatorAuthorizationResult authorize(
            OperatorAuthorizationRequest request
    );
}
