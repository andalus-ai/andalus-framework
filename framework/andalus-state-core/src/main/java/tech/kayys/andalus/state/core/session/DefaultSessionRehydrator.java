package tech.kayys.andalus.state.core.session;

import tech.kayys.andalus.state.session.AgentSession;
import tech.kayys.andalus.state.session.SessionCheckpoint;
import tech.kayys.andalus.state.session.SessionId;
import tech.kayys.andalus.state.session.SessionStore;
import tech.kayys.andalus.state.session.WorkflowReference;
import tech.kayys.andalus.state.session.rehydration.RehydratedSession;
import tech.kayys.andalus.state.session.rehydration.RehydrationPlan;
import tech.kayys.andalus.state.session.rehydration.SessionRehydrator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of SessionRehydrator using SessionStore and optimistic rehydration rules.
 */
public class DefaultSessionRehydrator implements SessionRehydrator {

    private final SessionStore sessionStore;

    public DefaultSessionRehydrator(SessionStore sessionStore) {
        this.sessionStore = Objects.requireNonNull(sessionStore, "sessionStore must not be null");
    }

    @Override
    public RehydrationPlan plan(SessionId session) {
        Objects.requireNonNull(session, "session must not be null");
        Optional<AgentSession> existing = sessionStore.get(session);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Cannot plan rehydration: session not found: " + session);
        }
        AgentSession agentSession = existing.get();
        SessionCheckpoint checkpoint = SessionCheckpoint.of(
                agentSession.id(),
                agentSession.version(),
                new WorkflowReference("wf-default", "1.0.0", "cp-root"),
                List.of(),
                List.of(agentSession.snapshot().contextSnapshotRef()),
                List.of(agentSession.snapshot().memoryGraphSnapshotRef()),
                agentSession.snapshot().artifactRefs(),
                "prov-default"
        );
        return RehydrationPlan.of(session, checkpoint);
    }

    @Override
    public RehydratedSession rehydrate(RehydrationPlan plan) {
        Objects.requireNonNull(plan, "plan must not be null");
        Optional<AgentSession> existing = sessionStore.get(plan.sessionId());
        if (existing.isEmpty()) {
            throw new IllegalStateException("Session not found during rehydration: " + plan.sessionId());
        }

        AgentSession session = existing.get();
        if (plan.revalidateAuthority()) {
            // Requires re-authorization before executing physical worker tasks
            return RehydratedSession.ready(session);
        }
        return RehydratedSession.ready(session);
    }
}
