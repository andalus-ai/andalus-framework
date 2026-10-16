package tech.kayys.andalus.spi.agent;

import java.util.Optional;

/**
 * Resolves the runtime profile applicable to an agent.
 */
public interface AgentProfileResolver {

    Optional<AgentProfile> resolve(
            AgentDefinitionReference definition,
            AgentProfileRequest request);

    record AgentDefinitionReference(
            AgentIdentity identity) {
    }

    record AgentProfileRequest(
            String profileId) {
    }
}
