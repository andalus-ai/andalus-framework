package tech.kayys.andalus.harness.memory;

/**
 * Status resulting from a memory admission or write attempt.
 */
public enum WriteStatus {
    ADMITTED,
    REJECTED,
    CONFLICT,
    POLICY_DENIED,
    CAPABILITY_UNAVAILABLE
}
