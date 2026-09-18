package tech.kayys.wayang.harness.coordination.messaging;

import tech.kayys.wayang.harness.protocol.AgentRef;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Governed message exchanged between agents without point-to-point hardcoded sockets.
 */
public record AgentMessage(
        String id,
        AgentRef sender,
        AgentRef recipient,
        String messageType,
        String payload,
        Instant timestamp
) {
    public AgentMessage {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(sender, "sender");
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(messageType, "messageType");
        payload = payload != null ? payload : "";
        timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public static AgentMessage of(AgentRef sender, AgentRef recipient, String messageType, String payload) {
        return new AgentMessage("msg-" + UUID.randomUUID(), sender, recipient, messageType, payload, Instant.now());
    }
}
