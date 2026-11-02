package tech.kayys.andalus.agent.capability;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Finds agents that advertise every requested capability.
     *
     * <p>This is an AND query: a profile is returned only when it supports
     * all identifiers in {@code capabilityIds}. Results are sorted by agent
     * ID so selection remains reproducible across implementations. Null,
     * empty, or malformed queries return an empty list rather than matching
     * every registered agent.</p>
     *
     * <p>The default implementation preserves compatibility for existing
     * directory providers. Distributed implementations may override it with
     * an indexed query.</p>
     *
     * @param capabilityIds required capability identifiers
     * @return matching profiles in deterministic ID order
     */
    default List<AgentProfile> findByCapabilities(Set<String> capabilityIds) {
        if (capabilityIds == null || capabilityIds.isEmpty()
                || capabilityIds.stream().anyMatch(id -> id == null || id.isBlank())) {
            return List.of();
        }

        Set<String> required = capabilityIds.stream()
                .map(String::trim)
                .collect(Collectors.toUnmodifiableSet());

        return findAll().stream()
                .filter(profile -> required.stream().allMatch(profile::supports))
                .sorted(Comparator.comparing(AgentProfile::id))
                .toList();
    }

    /**
     * Returns a snapshot of all currently registered profiles.
     */
    List<AgentProfile> findAll();
}
