package tech.kayys.andalus.context.assembly;

/**
 * Context projection contract for extracting a token-efficient view from session context representations.
 * Protects token efficiency by extracting only the pertinent slices, decisions, and observations.
 */
public interface ContextProjection {

    /**
     * Projects a governed ContextEnvelope tailored for the agent's current reasoning step.
     */
    ContextEnvelope project(String sessionId, ContextRequest request);
}
