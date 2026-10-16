package tech.kayys.andalus.a2a.handoff;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

/**
 * Orchestrates the complete agent handoff lifecycle:
 * 1. Policy evaluation (authorization)
 * 2. Context enrichment
 * 3. Routing to target agent
 * 4. Handoff result
 */
public final class AgentHandoffService {

    private final AgentHandoffPolicy policy;
    private final AgentHandoffRouter router;

    public AgentHandoffService(
            AgentHandoffPolicy policy,
            AgentHandoffRouter router) {
        this.policy = policy != null ? policy : AgentHandoffPolicy.allowAll();
        this.router = Objects.requireNonNull(router, "router");
    }

    public CompletionStage<HandoffResult> initiate(AgentHandoff handoff) {
        Objects.requireNonNull(handoff, "handoff");

        return policy.evaluate(handoff)
                .thenCompose(decision -> {
                    if (!decision.allowed()) {
                        return java.util.concurrent.CompletableFuture.completedFuture(
                                HandoffResult.rejected(
                                        handoff.handoffId(),
                                        handoff.targetAgentId(),
                                        decision.reason()));
                    }
                    return router.route(handoff);
                });
    }
}
