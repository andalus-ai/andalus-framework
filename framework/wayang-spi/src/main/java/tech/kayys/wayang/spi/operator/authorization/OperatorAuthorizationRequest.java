package tech.kayys.wayang.spi.operator.authorization;

import tech.kayys.wayang.spi.operator.OperatorContext;

/**
 * Request describing an operation to authorize.
 */
public record OperatorAuthorizationRequest(
        OperatorContext context,
        String permission,
        OperatorScope scope,
        String resourceType,
        String resourceId
) {

    public OperatorAuthorizationRequest {
        if (context == null) {
            throw new IllegalArgumentException("context is required");
        }

        if (permission == null || permission.isBlank()) {
            throw new IllegalArgumentException("permission is required");
        }

        if (scope == null) {
            throw new IllegalArgumentException("scope is required");
        }
    }

    public static OperatorAuthorizationRequest tenant(
            OperatorContext context,
            String permission,
            String resourceType,
            String resourceId
    ) {
        return new OperatorAuthorizationRequest(
                context,
                permission,
                OperatorScope.TENANT,
                resourceType,
                resourceId
        );
    }

    public static OperatorAuthorizationRequest platform(
            OperatorContext context,
            String permission,
            String resourceType,
            String resourceId
    ) {
        return new OperatorAuthorizationRequest(
                context,
                permission,
                OperatorScope.PLATFORM,
                resourceType,
                resourceId
        );
    }
}
