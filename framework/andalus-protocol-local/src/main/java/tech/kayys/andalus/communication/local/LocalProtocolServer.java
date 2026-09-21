package tech.kayys.andalus.communication.local;

import tech.kayys.andalus.communication.api.AgentMessage;
import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.capability.CapabilityId;
import tech.kayys.andalus.communication.message.MessageId;
import tech.kayys.andalus.communication.message.MessageType;
import tech.kayys.andalus.communication.protocol.ProtocolContext;
import tech.kayys.andalus.communication.protocol.ProtocolServer;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class LocalProtocolServer implements ProtocolServer {

    private final LocalAgentRegistry registry;

    public LocalProtocolServer(LocalAgentRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public CompletionStage<AgentMessage> receive(
            AgentMessage message,
            ProtocolContext context
    ) {
        Objects.requireNonNull(message, "message");
        var targetId = message.recipient().id();
        var handlerOpt = registry.find(targetId);

        if (handlerOpt.isEmpty()) {
            return CompletableFuture.completedFuture(
                    new AgentMessage(
                            MessageId.random(),
                            message.recipient(),
                            message.sender(),
                            MessageType.ERROR,
                            message.payload(),
                            Map.of("error", "Agent not found: " + targetId)
                    )
            );
        }

        var request = AgentRequest.of(
                message.recipient(),
                CapabilityId.of("invoke"),
                message.payload()
        );

        return handlerOpt.get().handle(request)
                .thenApply(response -> new AgentMessage(
                        MessageId.random(),
                        message.recipient(),
                        message.sender(),
                        response.success() ? MessageType.RESPONSE : MessageType.ERROR,
                        response.payload(),
                        response.metadata()
                ));
    }
}
