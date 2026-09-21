package tech.kayys.andalus.communication.local;

import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.api.AgentResponse;

import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface LocalAgentHandler {

    CompletionStage<AgentResponse> handle(
            AgentRequest request
    );
}
