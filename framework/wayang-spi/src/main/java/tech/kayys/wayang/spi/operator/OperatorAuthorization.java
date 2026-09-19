package tech.kayys.wayang.spi.operator;

import tech.kayys.wayang.spi.operator.authorization.OperatorAuthorizationRequest;
import tech.kayys.wayang.spi.operator.authorization.OperatorAuthorizationResult;

/**
 * Single reusable control-plane security boundary for all operator services.
 */
public interface OperatorAuthorization {

    /**
     * Authorize the given request.
     */
    OperatorAuthorizationResult authorize(OperatorAuthorizationRequest request);

    /**
     * Require that the operation described by request is authorized, or throw OperatorAuthorizationException.
     */
    default void require(OperatorAuthorizationRequest request) {
        OperatorAuthorizationResult result = authorize(request);
        if (!result.allowed()) {
            throw new OperatorAuthorizationException(
                    result.code(),
                    result.message()
            );
        }
    }

    /**
     * Require tenant-scoped permission.
     */
    default void require(OperatorContext context, String permission) {
        require(
                OperatorAuthorizationRequest.tenant(
                        context,
                        permission,
                        null,
                        null
                )
        );
    }

    /**
     * Require platform-scoped permission.
     */
    default void requirePlatform(OperatorContext context, String permission) {
        require(
                OperatorAuthorizationRequest.platform(
                        context,
                        permission,
                        null,
                        null
                )
        );
    }

    /**
     * Backwards-compatible overload for OperatorPermission tokens.
     */
    default void require(OperatorContext context, OperatorPermission permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is required");
        }
        require(context, permission.id());
    }
}
