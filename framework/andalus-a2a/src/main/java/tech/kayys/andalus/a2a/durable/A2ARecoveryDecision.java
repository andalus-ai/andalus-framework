package tech.kayys.andalus.a2a.durable;

/**
 * Known remote state of one A2A operation, as reported by an {@link A2AReconciler}
 * (6.16.6.7).
 *
 * <p>Result handling (6.16.6.8):</p>
 * <ul>
 *   <li>{@link #FOUND_RUNNING}   → restore/keep WAITING</li>
 *   <li>{@link #FOUND_COMPLETED} → persist completion, wake local execution</li>
 *   <li>{@link #FOUND_FAILED}    → persist failure, wake local execution</li>
 *   <li>{@link #FOUND_CANCELLED} → persist cancellation, wake local execution</li>
 *   <li>{@link #NOT_FOUND}       → evaluate idempotency/submission policy — never a
 *                                  blind resubmission; {@code NOT_FOUND ≠ FAILED}</li>
 *   <li>{@link #UNKNOWN}         → remain unresolved, retry reconciliation later</li>
 * </ul>
 */
public enum A2ARecoveryDecision {
    FOUND_RUNNING,
    FOUND_COMPLETED,
    FOUND_FAILED,
    FOUND_CANCELLED,
    NOT_FOUND,
    UNKNOWN
}
