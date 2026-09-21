package tech.kayys.andalus.communication.local;

import tech.kayys.andalus.communication.api.AgentRef;

import java.util.Optional;

public interface LocalAgentRegistry {

    AgentRef register(
            String agentId,
            String agentName,
            LocalAgentHandler handler
    );

    default AgentRef register(
            String agentId,
            LocalAgentHandler handler
    ) {
        return register(agentId, agentId, handler);
    }

    void unregister(String agentId);

    Optional<LocalAgentHandler> find(String agentId);

    boolean contains(String agentId);
}
