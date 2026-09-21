package tech.kayys.andalus.state.session;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import tech.kayys.andalus.execution.mobility.FencingToken;

/**
 * Durable session header governing execution authority, fencing, and ownership.
 */
public record SessionControlHeader(
    SessionId sessionId,
    String tenantId,
    String assignedWorkerId,
    FencingToken fencingToken,
    long stateRevision,
    SessionStatus status,
    Instant createdAt,
    Instant lastHeartbeatAt,
    Map<String, String> metadata
) implements Serializable {

    public SessionControlHeader {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(fencingToken, "fencingToken cannot be null");
        Objects.requireNonNull(status, "status cannot be null");
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static SessionControlHeader create(SessionId sessionId, String tenantId, String initialWorkerId) {
        return new SessionControlHeader(
            sessionId,
            tenantId,
            initialWorkerId,
            FencingToken.initial(),
            1L,
            SessionStatus.ACTIVE,
            Instant.now(),
            Instant.now(),
            Map.of()
        );
    }

    public SessionControlHeader withRevision(long nextRevision, FencingToken token) {
        return new SessionControlHeader(
            sessionId,
            tenantId,
            assignedWorkerId,
            token,
            nextRevision,
            status,
            createdAt,
            Instant.now(),
            metadata
        );
    }

    public SessionControlHeader withStatus(SessionStatus nextStatus) {
        return new SessionControlHeader(
            sessionId,
            tenantId,
            assignedWorkerId,
            fencingToken,
            stateRevision + 1,
            nextStatus,
            createdAt,
            Instant.now(),
            metadata
        );
    }

    public SessionControlHeader withWorker(String newWorkerId, FencingToken nextToken) {
        return new SessionControlHeader(
            sessionId,
            tenantId,
            newWorkerId,
            nextToken,
            stateRevision + 1,
            status,
            createdAt,
            Instant.now(),
            metadata
        );
    }

    public SessionControlHeader withHeartbeat() {
        return new SessionControlHeader(
            sessionId,
            tenantId,
            assignedWorkerId,
            fencingToken,
            stateRevision,
            status,
            createdAt,
            Instant.now(),
            metadata
        );
    }
}
