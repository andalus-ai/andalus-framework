package tech.kayys.wayang.state.core.session;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import tech.kayys.wayang.execution.mobility.FencingToken;
import tech.kayys.wayang.state.session.DurableSessionStoreSPI;
import tech.kayys.wayang.state.session.SessionControlHeader;
import tech.kayys.wayang.state.session.SessionId;
import tech.kayys.wayang.state.session.SessionSnapshot;
import tech.kayys.wayang.state.session.SessionStatus;

/**
 * Thread-safe, in-memory reference implementation of {@link DurableSessionStoreSPI}.
 * Strictly enforces fencing token monotonicity to prevent split-brain updates.
 */
public class InMemoryDurableSessionStore implements DurableSessionStoreSPI {

    private final Map<SessionId, SessionControlHeader> headers = new ConcurrentHashMap<>();
    private final Map<SessionId, List<SessionSnapshot>> snapshots = new ConcurrentHashMap<>();

    @Override
    public void initializeSession(SessionControlHeader header) {
        Objects.requireNonNull(header, "header cannot be null");
        headers.putIfAbsent(header.sessionId(), header);
    }

    @Override
    public Optional<SessionControlHeader> getHeader(SessionId sessionId) {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        return Optional.ofNullable(headers.get(sessionId));
    }

    @Override
    public void recordSnapshot(SessionSnapshot snapshot, FencingToken token) {
        Objects.requireNonNull(snapshot, "snapshot cannot be null");
        Objects.requireNonNull(token, "token cannot be null");

        SessionId sessionId = snapshot.header().sessionId();
        headers.compute(sessionId, (id, current) -> {
            if (current != null && current.fencingToken().isAfter(token)) {
                throw new IllegalStateException("Snapshot rejected: fencing token " + token +
                    " is stale compared to current " + current.fencingToken());
            }
            snapshots.computeIfAbsent(sessionId, k -> new CopyOnWriteArrayList<>()).add(snapshot);
            return snapshot.header().withRevision(
                (current != null ? current.stateRevision() : 0L) + 1, token);
        });
    }

    @Override
    public Optional<SessionSnapshot> getLatestSnapshot(SessionId sessionId) {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        List<SessionSnapshot> list = snapshots.get(sessionId);
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(list.size() - 1));
    }

    @Override
    public List<SessionSnapshot> listSnapshots(SessionId sessionId) {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        List<SessionSnapshot> list = snapshots.get(sessionId);
        return list != null ? List.copyOf(list) : List.of();
    }

    @Override
    public boolean updateLease(SessionId sessionId, String workerId, FencingToken token) {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(workerId, "workerId cannot be null");
        Objects.requireNonNull(token, "token cannot be null");

        var updated = headers.computeIfPresent(sessionId, (id, current) -> {
            if (current.fencingToken().isAfter(token)) {
                // Reject stale token
                return current;
            }
            return current.withWorker(workerId, token);
        });

        return updated != null && updated.assignedWorkerId().equals(workerId) &&
            updated.fencingToken().equals(token);
    }

    @Override
    public void transitionStatus(SessionId sessionId, SessionStatus expected, SessionStatus target, FencingToken token) {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(expected, "expected cannot be null");
        Objects.requireNonNull(target, "target cannot be null");
        Objects.requireNonNull(token, "token cannot be null");

        headers.compute(sessionId, (id, current) -> {
            if (current == null) {
                throw new IllegalArgumentException("Session not found: " + sessionId);
            }
            if (current.fencingToken().isAfter(token)) {
                throw new IllegalStateException("Status transition rejected: token " + token + " is stale");
            }
            if (current.status() != expected) {
                throw new IllegalStateException("Status transition precondition failed: expected " +
                    expected + " but was " + current.status());
            }
            return current.withStatus(target);
        });
    }
}
