package tech.kayys.wayang.state.session;

import java.util.List;
import java.util.Optional;
import tech.kayys.wayang.execution.mobility.FencingToken;

/**
 * Service Provider Interface for persisting, querying, and managing durable agent sessions.
 */
public interface DurableSessionStoreSPI {

    /**
     * Initializes a new durable session header.
     */
    void initializeSession(SessionControlHeader header);

    /**
     * Retrieves the latest control header for the session.
     */
    Optional<SessionControlHeader> getHeader(SessionId sessionId);

    /**
     * Records a new session snapshot guarded by the caller's fencing token.
     */
    void recordSnapshot(SessionSnapshot snapshot, FencingToken token);

    /**
     * Retrieves the latest recorded snapshot for the session.
     */
    Optional<SessionSnapshot> getLatestSnapshot(SessionId sessionId);

    /**
     * Lists historical snapshots for the session in chronological order.
     */
    List<SessionSnapshot> listSnapshots(SessionId sessionId);

    /**
     * Updates worker assignment and lease using a valid monotonically increasing fencing token.
     * Returns true if lease update succeeded, false if rejected due to stale token.
     */
    boolean updateLease(SessionId sessionId, String workerId, FencingToken token);

    /**
     * Atomically transitions the session status from expected to target status under a fencing token.
     */
    void transitionStatus(SessionId sessionId, SessionStatus expected, SessionStatus target, FencingToken token);
}
