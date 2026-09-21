package tech.kayys.andalus.state.session;

import java.util.List;
import java.util.Optional;

/**
 * High-level repository interface for managing durable agent sessions with optimistic concurrency.
 */
public interface SessionStore {

    /**
     * Finds an agent session by its unique logical identifier.
     */
    Optional<AgentSession> get(SessionId id);

    /**
     * Persists a new agent session.
     */
    void create(AgentSession session);

    /**
     * Updates an agent session state guarded by optimistic version check.
     */
    boolean updateIfVersion(SessionId id, SessionVersion expected, AgentSession next);

    /**
     * Retrieves all sessions eligible for resumption across workers.
     */
    List<AgentSession> findResumable();
}
