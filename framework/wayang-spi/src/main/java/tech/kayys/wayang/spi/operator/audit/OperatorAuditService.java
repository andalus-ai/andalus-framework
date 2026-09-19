package tech.kayys.wayang.spi.operator.audit;

import tech.kayys.wayang.spi.operator.OperatorContext;
import tech.kayys.wayang.spi.operator.authorization.OperatorScope;

import java.util.Map;

/**
 * Service for recording structured audit events from operator actions.
 */
public interface OperatorAuditService {

    void record(
            OperatorContext context,
            String operation,
            String permission,
            OperatorScope scope,
            String resourceType,
            String resourceId,
            boolean destructive,
            boolean privileged,
            OperatorAuditOutcome outcome,
            String authorizationCode,
            String message,
            Map<String, Object> before,
            Map<String, Object> after,
            Map<String, Object> attributes
    );

    default void record(
            OperatorContext context,
            String operation,
            String permission,
            OperatorScope scope,
            String resourceType,
            String resourceId,
            boolean destructive,
            boolean privileged,
            OperatorAuditOutcome outcome,
            String authorizationCode,
            String message
    ) {
        record(
                context,
                operation,
                permission,
                scope,
                resourceType,
                resourceId,
                destructive,
                privileged,
                outcome,
                authorizationCode,
                message,
                Map.of(),
                Map.of(),
                Map.of()
        );
    }
}
