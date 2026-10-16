package tech.kayys.andalus.a2a.handoff;

import java.util.concurrent.CompletionStage;

/**
 * Routes an authorized handoff to the appropriate target agent.
 * Decoupled from transport — can dispatch via A2A, local, or protocol-local.
 */
public interface AgentHandoffRouter {

    CompletionStage<HandoffResult> route(AgentHandoff handoff);
}
