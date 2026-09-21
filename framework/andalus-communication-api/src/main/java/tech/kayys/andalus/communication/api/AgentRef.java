package tech.kayys.andalus.communication.api;

import tech.kayys.andalus.communication.endpoint.AgentEndpoint;
import tech.kayys.andalus.communication.endpoint.LocalAgentEndpoint;
import tech.kayys.andalus.communication.endpoint.RemoteAgentEndpoint;

import java.net.URI;
import java.util.Objects;

public record AgentRef(
        String id,
        String name,
        AgentEndpoint endpoint
) {

    public AgentRef {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(endpoint, "endpoint");

        if (id.isBlank()) {
            throw new IllegalArgumentException(
                    "id must not be blank"
            );
        }
    }

    public static AgentRef local(String id, String name, String runtimeId) {
        return new AgentRef(id, name, LocalAgentEndpoint.of(runtimeId));
    }

    public static AgentRef remote(String id, String name, URI uri) {
        return new AgentRef(id, name, RemoteAgentEndpoint.of(uri));
    }
}
