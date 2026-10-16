package tech.kayys.andalus.a2a.handoff;

import java.util.concurrent.CompletionStage;

/**
 * Policy SPI for authorizing agent handoffs.
 * Prevents unauthorized agent-to-agent delegation.
 */
public interface AgentHandoffPolicy {

    CompletionStage<HandoffDecision> evaluate(AgentHandoff handoff);

    /** Allow all handoffs — suitable for development/testing */
    static AgentHandoffPolicy allowAll() {
        return handoff -> java.util.concurrent.CompletableFuture.completedFuture(
                HandoffDecision.allow());
    }
}
