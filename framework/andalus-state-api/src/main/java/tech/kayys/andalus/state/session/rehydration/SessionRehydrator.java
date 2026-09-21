package tech.kayys.andalus.state.session.rehydration;

import tech.kayys.andalus.state.session.SessionId;

/**
 * Service contract for planning and executing session resurrection across worker boundaries.
 */
public interface SessionRehydrator {

    /**
     * Constructs a rehydration plan for resurrecting the specified session.
     */
    RehydrationPlan plan(SessionId session);

    /**
     * Reconstructs and reauthorizes the session using the given plan.
     */
    RehydratedSession rehydrate(RehydrationPlan plan);
}
