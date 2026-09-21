package tech.kayys.andalus.state.core.session;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import tech.kayys.andalus.state.session.AgentSession;
import tech.kayys.andalus.state.session.SessionId;
import tech.kayys.andalus.state.session.SessionStatus;
import tech.kayys.andalus.state.session.SessionStore;
import tech.kayys.andalus.state.session.SessionVersion;

/**
 * Thread-safe in-memory implementation of SessionStore.
 */
public class InMemorySessionStore implements SessionStore {

    private final Map<SessionId, AgentSession> sessions = new ConcurrentHashMap<>();

    @Override
    public Optional<AgentSession> get(SessionId id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public void create(AgentSession session) {
        if (session == null) {
            throw new IllegalArgumentException("session must not be null");
        }
        sessions.putIfAbsent(session.id(), session);
    }

    @Override
    public synchronized boolean updateIfVersion(SessionId id, SessionVersion expected, AgentSession next) {
        if (id == null || expected == null || next == null) {
            return false;
        }
        AgentSession current = sessions.get(id);
        if (current == null) {
            return false;
        }
        if (current.version().value() != expected.value()) {
            return false;
        }
        sessions.put(id, next);
        return true;
    }

    @Override
    public List<AgentSession> findResumable() {
        return sessions.values().stream()
                .filter(s -> s.status() == SessionStatus.ACTIVE || s.status() == SessionStatus.SUSPENDED)
                .toList();
    }
}
