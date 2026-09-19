package tech.kayys.wayang.spi.operator.authorization;

/**
 * SPI policy for evaluating operator authorization requests.
 */
public interface OperatorAuthorizationPolicy {

    OperatorAuthorizationResult authorize(
            OperatorAuthorizationRequest request
    );
}
