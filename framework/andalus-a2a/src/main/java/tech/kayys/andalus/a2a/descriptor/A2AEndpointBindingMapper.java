package tech.kayys.andalus.a2a.descriptor;

import tech.kayys.andalus.communication.api.AgentEndpointDescriptor;

/**
 * Translates a Andalus {@link AgentEndpointDescriptor} into an {@link A2AInterface}.
 * Isolates protocol-specific endpoint translation.
 */
public interface A2AEndpointBindingMapper {

    boolean supports(AgentEndpointDescriptor endpoint);

    A2AInterface map(AgentEndpointDescriptor endpoint);
}
