package tech.kayys.andalus.execution.sideeffect;

public enum RecoveryBehavior {
    REPLAY,
    SKIP_IF_COMMITTED,
    REQUIRE_CONFIRMATION,
    ABORT,
    RECONCILE
}
