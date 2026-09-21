package tech.kayys.andalus.execution.sideeffect;

public enum SideEffectClass {
    PURE,
    IDEMPOTENT,
    TRANSACTIONAL,
    NON_IDEMPOTENT,
    UNKNOWN
}
