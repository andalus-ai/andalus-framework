package tech.kayys.andalus.communication.protocol;

import tech.kayys.andalus.communication.api.AgentMessage;

import java.util.concurrent.CompletionStage;

public interface ProtocolServer {

    CompletionStage<AgentMessage> receive(
            AgentMessage message,
            ProtocolContext context
    );
}
