package tech.kayys.andalus.state.session;

import java.util.Objects;

/**
 * Durable logical identity of an agent session decoupled from temporary physical executions.
 */
public record SessionIdentity(
        SessionId session,
        String workloadId,
        String ownerId,
        String agentId) {

    public SessionIdentity {
        Objects.requireNonNull(session, "session must not be null");
        workloadId = workloadId != null ? workloadId : "default-workload";
        ownerId = ownerId != null ? ownerId : "anonymous";
        agentId = agentId != null ? agentId : "default-agent";
    }

    public static SessionIdentity of(SessionId session, String workloadId, String ownerId, String agentId) {
        return new SessionIdentity(session, workloadId, ownerId, agentId);
    }
}
