package tech.kayys.wayang.execution.sideeffect;

public enum SideEffectClass {
    PURE,
    IDEMPOTENT,
    TRANSACTIONAL,
    NON_IDEMPOTENT,
    UNKNOWN
}
