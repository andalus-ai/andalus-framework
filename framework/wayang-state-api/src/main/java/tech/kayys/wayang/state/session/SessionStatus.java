package tech.kayys.wayang.state.session;

/**
 * Lifecycle states of an agent execution session.
 */
public enum SessionStatus {
    /**
     * Session is actively executing or ready to execute.
     */
    ACTIVE,

    /**
     * Session execution is paused by operator or policy.
     */
    SUSPENDED,

    /**
     * Session is migrating across workers or trust domains.
     */
    MIGRATING,

    /**
     * Session is serialized to cold storage awaiting future reactivation.
     */
    HIBERNATED,

    /**
     * Session has completed all goals successfully.
     */
    COMPLETED,

    /**
     * Session was cancelled or explicitly terminated.
     */
    TERMINATED,

    /**
     * Session encountered an unrecoverable failure.
     */
    FAILED
}
