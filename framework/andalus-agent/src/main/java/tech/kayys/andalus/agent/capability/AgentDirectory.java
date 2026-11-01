package tech.kayys.andalus.agent.capability;

import java.util.List;
import java.util.Optional;

/**
 * Registry contract for discovering agents by identity or advertised capability.
 *
 * <p>Implementations may be local or distributed. Returned profiles are
 * descriptive metadata only: callers must still resolve the live agent,
 * check its availability, enforce delegation policy, and authorize the
 * requested operation before execution.</p>
 */
public interface AgentDirectory {

    /**
     * Adds or replaces the profile for an agent identifier.
     *
     * @param profile immutable discovery profile
     */
    void register(AgentProfile profile);

    /**
     * Removes an agent profile.
     *
     * @param agentId stable agent identifier
     * @return {@code true} when a profile was removed
     */
    boolean unregister(String agentId);

    /**
     * Finds an agent by its stable identifier.
     */
    Optional<AgentProfile> findById(String agentId);

    /**
     * Finds agents that explicitly advertise the given capability identifier.
     *
     * <p>Implementations should return deterministic ordering to make
     * selection reproducible.</p>
     */
    List<AgentProfile> findByCapability(String capabilityId);

    /**
     * Returns a snapshot of all currently registered profiles.
     */
    List<AgentProfile> findAll();
}
