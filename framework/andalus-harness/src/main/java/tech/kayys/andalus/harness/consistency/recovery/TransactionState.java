package tech.kayys.andalus.harness.consistency.recovery;

/**
 * State of a scoped runtime transaction.
 */
public enum TransactionState {
    ACTIVE,
    COMMITTED,
    ROLLED_BACK,
    COMPENSATED,
    FAILED
}
