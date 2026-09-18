package tech.kayys.wayang.harness.semantics;

/**
 * Reach and blast radius of an operation's side effects.
 */
public enum EffectScope {
    EXECUTION,
    SESSION,
    WORKSPACE,
    LOCAL_SYSTEM,
    NETWORK,
    EXTERNAL_SYSTEM,
    GLOBAL
}
