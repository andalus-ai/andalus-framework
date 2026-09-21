package tech.kayys.andalus.state.session.rehydration;

import tech.kayys.andalus.state.session.AgentSession;
import tech.kayys.andalus.state.session.SessionId;

import java.util.Objects;

/**
 * Encapsulates a rehydrated agent session ready for physical execution dispatch.
 */
public record RehydratedSession(
        SessionId sessionId,
        AgentSession session,
        boolean requiresReauthorization,
        String statusMessage) {

    public RehydratedSession {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(session, "session must not be null");
        statusMessage = statusMessage != null ? statusMessage : "Session rehydrated successfully";
    }

    public static RehydratedSession ready(AgentSession session) {
        return new RehydratedSession(session.id(), session, false, "Ready for execution");
    }

    public static RehydratedSession requiresAuth(AgentSession session, String reason) {
        return new RehydratedSession(session.id(), session, true, reason);
    }
}
