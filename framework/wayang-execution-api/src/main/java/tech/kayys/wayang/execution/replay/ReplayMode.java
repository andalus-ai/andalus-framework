package tech.kayys.wayang.execution.replay;

/**
 * Operational modes for execution replay and simulation in Wayang v4.36.
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
