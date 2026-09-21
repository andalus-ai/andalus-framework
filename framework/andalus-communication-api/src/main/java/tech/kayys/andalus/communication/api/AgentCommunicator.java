package tech.kayys.andalus.communication.api;

import tech.kayys.andalus.communication.protocol.ProtocolContext;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

public interface AgentCommunicator {

    CompletionStage<AgentResponse> send(
            AgentRequest request,
            ProtocolContext context
    );

    default CompletionStage<AgentResponse> send(AgentRequest request) {
        return send(request, ProtocolContext.empty());
    }

    AgentTask submit(
            AgentRequest request,
            ProtocolContext context
    );

    default AgentTask submit(AgentRequest request) {
        return submit(request, ProtocolContext.empty());
    }

    Flow.Publisher<AgentEvent> stream(
            AgentRequest request,
            ProtocolContext context
    );

    default Flow.Publisher<AgentEvent> stream(AgentRequest request) {
        return stream(request, ProtocolContext.empty());
    }
}
