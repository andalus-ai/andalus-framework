package tech.kayys.wayang.harness.isolation;

/**
 * State machine of a resource lease.
 */
public enum LeaseState {
    REQUESTED,
    RESERVED,
    ALLOCATED,
    ACTIVE,
    REVOKING,
    RELEASED,
    EXPIRED
}
