package tech.kayys.andalus.security.communication;

import tech.kayys.andalus.communication.api.*;
import tech.kayys.andalus.communication.protocol.ProtocolContext;
import tech.kayys.andalus.security.authz.AuthorizationRequest;
import tech.kayys.andalus.security.authz.AuthorizationDecision;
import tech.kayys.andalus.security.authz.AuthorizationService;
import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.delegation.DelegationContext;
import tech.kayys.andalus.security.exception.AuthorizationException;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

/**
 * A decorator around {@link AgentCommunicator} that enforces authorization
 * before delegating to the underlying communicator.
 *
 * <p>The security context and optional delegation are read from trusted
 * protocol attributes. Missing identity is treated as anonymous.</p>
 */
public final class SecuredAgentCommunicator implements AgentCommunicator {

    public static final String SECURITY_CONTEXT_KEY = "andalus.security.context";
    public static final String DELEGATION_CONTEXT_KEY = "andalus.security.delegation";

    private final AgentCommunicator delegate;
    private final AuthorizationService authorizationService;

    public SecuredAgentCommunicator(
            AgentCommunicator delegate,
            AuthorizationService authorizationService
    ) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.authorizationService = Objects.requireNonNull(authorizationService, "authorizationService");
    }

    @Override
    public CompletionStage<AgentResponse> send(AgentRequest request, ProtocolContext context) {
        return authorize(request, context).thenCompose(decision -> {
            if (!decision.allowed()) {
                return CompletableFuture.failedFuture(
                        new AuthorizationException("Request denied: " + decision.reason()));
            }
            return delegate.send(request, context);
        });
    }

    @Override
    public AgentTask submit(AgentRequest request, ProtocolContext context) {
        AuthorizationDecision decision = authorizeSync(request, context);
        if (!decision.allowed()) {
            throw new AuthorizationException("Request denied: " + decision.reason());
        }
        return delegate.submit(request, context);
    }

    @Override
    public Flow.Publisher<AgentEvent> stream(AgentRequest request, ProtocolContext context) {
        AuthorizationDecision decision = authorizeSync(request, context);
        if (!decision.allowed()) {
            throw new AuthorizationException("Request denied: " + decision.reason());
        }
        return delegate.stream(request, context);
    }

    private CompletionStage<AuthorizationDecision> authorize(AgentRequest request, ProtocolContext context) {
        SecurityContext secCtx = extractSecurityContext(context);
        Optional<DelegationContext> delegation = extractDelegationContext(context);
        AuthorizationRequest authRequest = AuthorizationRequest.execute(
                secCtx,
                request.capability().value(),
                request.target().id(),
                delegation
        );
        return authorizationService.authorize(authRequest);
    }

    private AuthorizationDecision authorizeSync(AgentRequest request, ProtocolContext context) {
        try {
            return authorize(request, context).toCompletableFuture().join();
        } catch (java.util.concurrent.CompletionException ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            throw new AuthorizationException("Authorization failed: " + cause.getMessage(), cause);
        }
    }

    private SecurityContext extractSecurityContext(ProtocolContext context) {
        if (context == null) {
            return SecurityContext.anonymous();
        }
        Object value = context.attributes().get(SECURITY_CONTEXT_KEY);
        return value instanceof SecurityContext securityContext
                ? securityContext
                : SecurityContext.anonymous();
    }

    private Optional<DelegationContext> extractDelegationContext(ProtocolContext context) {
        if (context == null) {
            return Optional.empty();
        }
        Object value = context.attributes().get(DELEGATION_CONTEXT_KEY);
        return value instanceof DelegationContext delegation
                ? Optional.of(delegation)
                : Optional.empty();
    }
}
