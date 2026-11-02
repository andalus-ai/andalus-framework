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

    /** Metadata key used to mark a profile as explicitly public. */
    String VISIBILITY_METADATA_KEY = "andalus.visibility";

    /** Metadata key containing the tenant that owns a tenant-scoped profile. */
    String TENANT_ID_METADATA_KEY = "andalus.tenant-id";

    /** Explicit public visibility value for {@link #VISIBILITY_METADATA_KEY}. */
    String PUBLIC_VISIBILITY = "public";

    void register(AgentProfile profile);

    boolean unregister(String agentId);

    Optional<AgentProfile> findById(String agentId);

    List<AgentProfile> findByCapability(String capabilityId);

    /**
     * Finds agents that advertise every requested capability (AND semantics).
     * Null, empty, or malformed queries return an empty list. Results are
     * ordered by agent ID.
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
     * Finds profiles visible to the supplied tenant.
     *
     * <p>A profile is visible when it is explicitly public using metadata
     * {@code andalus.visibility=public}, or when its
     * {@code andalus.tenant-id} metadata exactly matches the non-blank tenant
     * ID. A missing tenant ID therefore returns public profiles only.
     * Profiles with neither marker are not exposed by this query.</p>
     *
     * <p>This is a discovery boundary, not an authorization decision. The
     * runtime must still resolve the live agent and enforce invocation policy.</p>
     *
     * @param tenantId requesting tenant, or {@code null} for public profiles only
     * @return visible profiles in deterministic ID order
     */
    default List<AgentProfile> findVisibleToTenant(String tenantId) {
        String normalizedTenantId = tenantId == null ? null : tenantId.trim();
        if (normalizedTenantId != null && normalizedTenantId.isEmpty()) {
            normalizedTenantId = null;
        }
        final String tenant = normalizedTenantId;

        return findAll().stream()
                .filter(profile -> PUBLIC_VISIBILITY.equals(
                                profile.metadata().get(VISIBILITY_METADATA_KEY))
                        || (tenant != null && tenant.equals(
                                profile.metadata().get(TENANT_ID_METADATA_KEY))))
                .sorted(Comparator.comparing(AgentProfile::id))
                .toList();
    }

    List<AgentProfile> findAll();
}
