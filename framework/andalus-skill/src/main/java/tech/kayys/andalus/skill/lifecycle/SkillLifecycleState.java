package tech.kayys.andalus.skill.lifecycle;

/**
 * Management lifecycle states of a Skill installation.
 *
 * <p>These states describe <em>administrative availability</em>, not execution
 * state. Execution states (RUNNING, COMPLETED, FAILED) belong to
 * {@code SkillRuntime} and {@code SkillExecutionState}.</p>
 *
 * <pre>
 * DISCOVERED → VALIDATED → INSTALLED → REGISTERED → ACTIVE
 *                                                      ↓
 *                                                  DISABLED
 *                                                      ↓
 *                                                 UNINSTALLED
 *
 * Any state → FAILED (installation/validation error)
 * </pre>
 */
public enum SkillLifecycleState {

    /** Package is known to the system but not yet validated. */
    DISCOVERED,

    /** Manifest, signatures, and dependencies have been verified. */
    VALIDATED,

    /** Package is stored locally; not yet registered for resolution. */
    INSTALLED,

    /** Registered in the SkillRegistry; available for resolution but not yet active. */
    REGISTERED,

    /** Active — resolver may return this Skill for matching requests. */
    ACTIVE,

    /** Administratively disabled; resolver will skip this Skill. */
    DISABLED,

    /** Installation or activation failed; no new executions will be started. */
    FAILED,

    /** Package removed from registry and storage. */
    UNINSTALLED
}
