package tech.kayys.andalus.agent.capability;

/** Availability state published by one live agent runtime registration. */
public enum AgentRuntimeStatus {
    /** Runtime has registered but has not completed readiness checks. */
    STARTING,
    /** Runtime is ready to receive authorized invocations. */
    AVAILABLE,
    /** Runtime is alive but temporarily unable to receive invocations. */
    UNAVAILABLE,
    /** Registration has been explicitly revoked. */
    REVOKED
}
