package tech.kayys.andalus.execution.economics;

/**
 * Lifecycle state of a resource reservation.
 */
public enum ReservationState {
    RESERVED,
    ALLOCATED,
    RELEASED,
    EXPIRED
}
