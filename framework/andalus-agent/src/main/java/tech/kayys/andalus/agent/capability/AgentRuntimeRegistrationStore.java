package tech.kayys.andalus.agent.capability;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Persistence-neutral contract for live agent runtime registration.
 *
 * <p>Implementations must make acquisition/takeover atomic and issue a
 * durable, monotonically increasing generation for each new owner. Renew,
 * status publication, and revoke operations must compare the complete token
 * (tenant, agent, instance, owner, generation); stale tokens must not mutate
 * a newer registration.</p>
 *
 * <p>Lease expiry decisions are made by the implementation's authoritative
 * clock, normally the database clock for a distributed store. Callers must
 * not decide ownership by comparing {@link AgentRuntimeLease#expiresAt()}
 * with a local clock. A returned lease is a snapshot, not an authorization
 * grant. Invocation authorization remains a separate runtime concern.</p>
 *
 * <p>All methods are tenant-scoped. Implementations must never return a
 * registration belonging to another tenant.</p>
 */
public interface AgentRuntimeRegistrationStore {

    /**
     * Atomically acquires a new registration or takes over an expired one.
     * Returns empty while a different, unexpired owner holds the identity.
     * A successful acquisition starts in {@link AgentRuntimeStatus#STARTING}.
     */
    Optional<AgentRuntimeLease> acquire(AgentRuntimeRegistrationRequest request);

    /**
     * Extends the lease only when the supplied token is still the current,
     * unexpired owner. Returns empty for expired or stale tokens.
     */
    Optional<AgentRuntimeLease> renew(AgentRuntimeLeaseToken token, Duration leaseDuration);

    /**
     * Publishes readiness only for the current, unexpired owner and renews
     * its lease atomically. REVOKED is terminal and must use {@link #revoke}.
     */
    Optional<AgentRuntimeLease> publishStatus(
            AgentRuntimeLeaseToken token,
            AgentRuntimeStatus status,
            Duration leaseDuration);

    /**
     * Revokes only the exact current owner. A stale generation must not
     * revoke a successor registration.
     */
    boolean revoke(AgentRuntimeLeaseToken token);

    /**
     * Returns currently live registrations for this tenant and logical agent.
     * Expired and revoked registrations must be excluded.
     */
    List<AgentRuntimeLease> findActive(String tenantId, String agentId);
}
