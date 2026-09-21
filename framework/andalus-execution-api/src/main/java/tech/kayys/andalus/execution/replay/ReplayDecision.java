package tech.kayys.andalus.execution.replay;

/**
 * Authorization decisions for replay execution.
 */
public enum ReplayDecision {
    ALLOW,
    ALLOW_SIMULATION_ONLY,
    ALLOW_DRY_RUN,
    DENY,
    REQUIRE_APPROVAL;

    public boolean isPermitted() {
        return this == ALLOW || this == ALLOW_SIMULATION_ONLY || this == ALLOW_DRY_RUN;
    }
}
