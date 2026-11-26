package tech.kayys.andalus.agent.capability;

import java.time.Instant;
import java.util.Objects;

/**
 * Snapshot of a registered agent runtime and its current fencing generation.
 *
 * @param token ownership token; all mutations must present this exact token
 * @param status published runtime readiness state
 * @param expiresAt lease expiry according to the backing store's clock
 */
public record AgentRuntimeLease(
        AgentRuntimeLeaseToken token,
        AgentRuntimeStatus status,
        Instant expiresAt) {

    public AgentRuntimeLease {
        Objects.requireNonNull(token, "token must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    }
}
