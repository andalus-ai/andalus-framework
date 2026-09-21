package tech.kayys.andalus.execution.replay;

/**
 * Operational modes for execution replay and simulation in Andalus v4.36.
 */
public enum ReplayMode {
    INSPECT,
    RECONSTRUCT,
    SIMULATE,
    DRY_RUN,
    REEXECUTE;

    public boolean allowsExternalSideEffects() {
        return this == REEXECUTE;
    }
}
