package tech.kayys.andalus.a2a.execution;

import tech.kayys.andalus.communication.api.AgentRef;
import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.api.CommunicationOptions;
import tech.kayys.andalus.communication.api.RequestMode;
import tech.kayys.andalus.communication.capability.CapabilityId;
import tech.kayys.andalus.communication.message.MessagePayload;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * Maps incoming A2A transport payloads into canonical {@link AgentRequest}s.
 */
public final class A2ARequestMapper {

    private A2ARequestMapper() {}

    public static AgentRequest toAgentRequest(
            AgentRef targetAgent,
            String capability,
            String textPayload,
            Map<String, Object> metadata
    ) {
        Objects.requireNonNull(targetAgent, "targetAgent");
        CapabilityId capId = capability != null ? CapabilityId.of(capability) : CapabilityId.of("default");
        MessagePayload payload = textPayload != null ? MessagePayload.text(textPayload) : MessagePayload.text("");

        return new AgentRequest(
                targetAgent,
                capId,
                payload,
                RequestMode.AUTO,
                Duration.ofSeconds(60),
                CommunicationOptions.automatic(),
                metadata != null ? metadata : Map.of()
        );
    }
}
