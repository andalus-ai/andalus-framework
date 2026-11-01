package tech.kayys.andalus.agent.capability;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe, process-local implementation of {@link AgentDirectory}.
 *
 * <p>This implementation is intended for embedded applications and tests.
 * Distributed deployments can provide another implementation backed by a
 * shared registry without changing consumers of the directory contract.</p>
 */
public final class InMemoryAgentDirectory implements AgentDirectory {

    private static final Comparator<AgentProfile> PROFILE_ORDER =
            Comparator.comparing(AgentProfile::id);

    private final ConcurrentMap<String, AgentProfile> profiles = new ConcurrentHashMap<>();

    @Override
    public void register(AgentProfile profile) {
        Objects.requireNonNull(profile, "profile must not be null");
        profiles.put(profile.id(), profile);
    }

    @Override
    public boolean unregister(String agentId) {
        if (agentId == null || agentId.isBlank()) {
            return false;
        }
        return profiles.remove(agentId.trim()) != null;
    }

    @Override
    public Optional<AgentProfile> findById(String agentId) {
        if (agentId == null || agentId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(profiles.get(agentId.trim()));
    }

    @Override
    public List<AgentProfile> findByCapability(String capabilityId) {
        if (capabilityId == null || capabilityId.isBlank()) {
            return List.of();
        }
        String normalized = capabilityId.trim();
        return profiles.values().stream()
                .filter(profile -> profile.supports(normalized))
                .sorted(PROFILE_ORDER)
                .toList();
    }

    @Override
    public List<AgentProfile> findAll() {
        return profiles.values().stream()
                .sorted(PROFILE_ORDER)
                .toList();
    }
}
