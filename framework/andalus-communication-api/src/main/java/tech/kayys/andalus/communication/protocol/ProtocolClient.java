package tech.kayys.andalus.communication.protocol;

import tech.kayys.andalus.communication.api.AgentEvent;
import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.api.AgentResponse;
import tech.kayys.andalus.communication.api.AgentTask;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

public interface ProtocolClient {

    CompletionStage<AgentResponse> send(
            AgentRequest request,
            ProtocolContext context
    );

    AgentTask submit(
            AgentRequest request,
            ProtocolContext context
    );

    Flow.Publisher<AgentEvent> stream(
            AgentRequest request,
            ProtocolContext context
    );
}
