package tech.kayys.andalus.a2a.handoff;

/**
 * Reason for initiating an agent handoff.
 * Structured rather than free-text to enable policy-based routing.
 */
public enum HandoffReason {
    /** Target agent has a specific skill this agent lacks */
    SKILL_DELEGATION,
    /** Explicit user request to speak to a specific agent */
    USER_REDIRECT,
    /** Execution capacity exceeded; delegating to specialized agent */
    LOAD_SHEDDING,
    /** Agent escalates to a higher-authority agent */
    ESCALATION,
    /** Task completed; handoff to finalization agent */
    COMPLETION_HANDOFF,
    /** Agent determined it cannot handle this query */
    OUT_OF_SCOPE,
    /** Routing based on language/locale requirements */
    LOCALE_ROUTING,
    /** Routing based on tenant/domain */
    DOMAIN_ROUTING
}
