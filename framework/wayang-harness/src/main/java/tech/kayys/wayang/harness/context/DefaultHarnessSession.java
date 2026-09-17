package tech.kayys.wayang.harness.context;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Immutable value record implementing {@link HarnessSession}.
 */
public record DefaultHarnessSession(
        String sessionId,
        Optional<String> parentSessionId,
        long version,
        SessionState state
) implements HarnessSession {

    public DefaultHarnessSession {
        sessionId = Objects.requireNonNull(sessionId, "sessionId");
        parentSessionId = parentSessionId == null ? Optional.empty() : parentSessionId;
        state = state == null ? SessionState.NEW : state;
    }

    public static DefaultHarnessSession createNew() {
        return new DefaultHarnessSession("session-" + UUID.randomUUID(), Optional.empty(), 1L, SessionState.NEW);
    }

    public static DefaultHarnessSession of(String sessionId) {
        return new DefaultHarnessSession(sessionId, Optional.empty(), 1L, SessionState.ACTIVE);
    }

    public DefaultHarnessSession incrementVersion() {
        return new DefaultHarnessSession(sessionId, parentSessionId, version + 1, state);
    }

    public DefaultHarnessSession withState(SessionState nextState) {
        return new DefaultHarnessSession(sessionId, parentSessionId, version + 1, nextState);
    }
}
