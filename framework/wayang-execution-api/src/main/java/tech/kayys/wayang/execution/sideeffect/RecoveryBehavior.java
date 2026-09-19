package tech.kayys.wayang.execution.sideeffect;

public enum RecoveryBehavior {
    REPLAY,
    SKIP_IF_COMMITTED,
    REQUIRE_CONFIRMATION,
    ABORT,
    RECONCILE
}
