package tech.kayys.andalus.state.session;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Logical representation of a durable agent session.
 * An execution can die without the agent session dying.
 */
public record AgentSession(
        SessionId id,
        SessionIdentity identity,
        SessionStatus status,
        SessionVersion version,
        SessionSnapshot snapshot,
        Instant createdAt,
        Instant updatedAt) {

    public AgentSession {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(identity, "identity must not be null");
        Objects.requireNonNull(status, "status must not be null");
        version = version != null ? version : SessionVersion.initial();
        createdAt = createdAt != null ? createdAt : Instant.now();
        updatedAt = updatedAt != null ? updatedAt : createdAt;
    }

    public static AgentSession create(SessionIdentity identity, SessionSnapshot initialSnapshot) {
        return new AgentSession(
                identity.session(),
                identity,
                SessionStatus.ACTIVE,
                SessionVersion.initial(),
                initialSnapshot,
                Instant.now(),
                Instant.now());
    }

    public AgentSession withSnapshot(SessionSnapshot newSnapshot) {
        return new AgentSession(
                id,
                identity,
                status,
                version.next(),
                newSnapshot,
                createdAt,
                Instant.now());
    }

    public AgentSession withStatus(SessionStatus newStatus) {
        return new AgentSession(
                id,
                identity,
                newStatus,
                version.next(),
                snapshot,
                createdAt,
                Instant.now());
    }
}
