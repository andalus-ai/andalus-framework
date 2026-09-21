package tech.kayys.andalus.communication.api;

import tech.kayys.andalus.communication.message.MessageId;
import tech.kayys.andalus.communication.message.MessagePayload;
import tech.kayys.andalus.communication.message.MessageType;

import java.util.Map;
import java.util.Objects;

public record AgentMessage(
        MessageId id,
        AgentRef sender,
        AgentRef recipient,
        MessageType type,
        MessagePayload payload,
        Map<String, Object> metadata
) {

    public AgentMessage {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(sender, "sender");
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(payload, "payload");

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
