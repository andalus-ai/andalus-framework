package tech.kayys.andalus.security.enforcement;

import tech.kayys.andalus.security.context.SecurityContext;
import java.util.concurrent.CompletionStage;

/**
 * Functional adapter around {@link PolicyEnforcementPoint} for ergonomic security enforcement.
 */
@FunctionalInterface
public interface SecuredOperation {

    CompletionStage<EnforcementResult> enforce(
            SecurityContext context,
            String capability,
            String action,
            String resource,
            Object payload
    );

    static SecuredOperation from(PolicyEnforcementPoint pep) {
        return (context, capability, action, resource, payload) ->
                pep.enforce(EnforcementRequest.of(context, capability, action, resource, payload));
    }
}
