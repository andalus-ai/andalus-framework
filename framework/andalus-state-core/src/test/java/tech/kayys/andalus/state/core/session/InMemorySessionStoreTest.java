package tech.kayys.andalus.state.core.session;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.state.session.AgentSession;
import tech.kayys.andalus.state.session.SessionControlHeader;
import tech.kayys.andalus.state.session.SessionId;
import tech.kayys.andalus.state.session.SessionIdentity;
import tech.kayys.andalus.state.session.SessionSnapshot;
import tech.kayys.andalus.state.session.SessionStatus;
import tech.kayys.andalus.state.session.SessionVersion;

class InMemorySessionStoreTest {

    @Test
    void testCreateGetAndUpdateIfVersion() {
        InMemorySessionStore store = new InMemorySessionStore();
        SessionId id = SessionId.generate();
        SessionIdentity identity = SessionIdentity.of(id, "workload-1", "user-1", "agent-1");
        SessionControlHeader header = SessionControlHeader.create(id, "tenant-1", "worker-1");
        SessionSnapshot snapshot = SessionSnapshot.of(header, "ctx-ref-1", "mem-ref-1", List.of());

        AgentSession session = AgentSession.create(identity, snapshot);
        store.create(session);

        Optional<AgentSession> fetched = store.get(id);
        assertTrue(fetched.isPresent());
        assertEquals(SessionStatus.ACTIVE, fetched.get().status());
        assertEquals(SessionVersion.initial().value(), fetched.get().version().value());

        // Concurrent update with wrong expected version should fail
        AgentSession nextSession = session.withStatus(SessionStatus.SUSPENDED);
        boolean staleUpdate = store.updateIfVersion(id, SessionVersion.of(999L), nextSession);
        assertFalse(staleUpdate);

        // Update with matching expected version succeeds
        boolean validUpdate = store.updateIfVersion(id, SessionVersion.initial(), nextSession);
        assertTrue(validUpdate);

        Optional<AgentSession> updated = store.get(id);
        assertTrue(updated.isPresent());
        assertEquals(SessionStatus.SUSPENDED, updated.get().status());

        List<AgentSession> resumable = store.findResumable();
        assertEquals(1, resumable.size());
    }
}
