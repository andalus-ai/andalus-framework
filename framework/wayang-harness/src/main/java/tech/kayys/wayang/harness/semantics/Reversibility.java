package tech.kayys.wayang.harness.semantics;

/**
 * Declares how reversibly or compensatably an operation behaves on failure.
 */
public enum Reversibility {
    PURE,
    REVERSIBLE,
    COMPENSATABLE,
    NON_REVERSIBLE
}
