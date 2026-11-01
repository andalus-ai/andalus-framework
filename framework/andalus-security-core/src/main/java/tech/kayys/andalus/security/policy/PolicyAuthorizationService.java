package tech.kayys.andalus.security.policy;

import tech.kayys.andalus.security.authz.AuthorizationDecision;
import tech.kayys.andalus.security.authz.AuthorizationRequest;
import tech.kayys.andalus.security.authz.AuthorizationService;
import tech.kayys.andalus.security.delegation.DelegationContext;
import tech.kayys.andalus.security.policy.engine.PolicyEngine;

import java.time.Clock;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * {@link AuthorizationService} backed by a {@link PolicyEngine}.
 * Delegation constraints are checked before policy evaluation so a policy
 * allow cannot widen authority delegated by an upstream principal.
 */
public final class PolicyAuthorizationService implements AuthorizationService {

    private final PolicyEngine engine;
    private final Clock clock;

    public PolicyAuthorizationService(PolicyEngine engine) {
        this(engine, Clock.systemUTC());
    }

    public PolicyAuthorizationService(PolicyEngine engine, Clock clock) {
        this.engine = Objects.requireNonNull(engine, "engine");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public CompletionStage<AuthorizationDecision> authorize(AuthorizationRequest request) {
        Objects.requireNonNull(request, "request");

        if (request.delegation().isPresent()) {
            AuthorizationDecision delegationDecision = evaluateDelegation(request, request.delegation().get());
            if (!delegationDecision.allowed()) {
                return CompletableFuture.completedFuture(delegationDecision);
            }
        }

        PolicyDecision decision = engine.evaluate(request);
        Map<String, Object> obligations = Map.of("obligations", decision.obligations());

        return CompletableFuture.completedFuture(new AuthorizationDecision(
                decision.allowed(), decision.reason(), obligations));
    }

    private AuthorizationDecision evaluateDelegation(
            AuthorizationRequest request,
            DelegationContext delegation
    ) {
        if (delegation.isExpired(clock.instant())) {
            return AuthorizationDecision.deny("Delegation expired");
        }

        if (!delegation.constraints().permitsCapability(request.capability())) {
            return AuthorizationDecision.deny("Capability is outside delegated authority");
        }

        if (!delegation.constraints().permitsAction(request.action())) {
            return AuthorizationDecision.deny("Action is outside delegated authority");
        }

        if (request.resource() != null && !request.resource().isBlank()
                && !delegation.audience().allows(request.resource())) {
            return AuthorizationDecision.deny("Resource is outside delegation audience");
        }

        return AuthorizationDecision.allow("Delegation constraints satisfied");
    }
}
