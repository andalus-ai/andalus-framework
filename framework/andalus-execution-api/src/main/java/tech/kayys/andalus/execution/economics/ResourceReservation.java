package tech.kayys.andalus.execution.economics;

import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.resource.model.ResourceType;

import java.time.Instant;
import java.util.Objects;

/**
 * Time-bounded resource reservation held on behalf of an execution.
 */
public record ResourceReservation(
    ReservationId id,
    ExecutionIdentity executionId,
    ResourceType type,
    double amount,
    Instant expiresAt,
    ReservationState state
) {
    public ResourceReservation {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(expiresAt, "expiresAt cannot be null");
        Objects.requireNonNull(state, "state cannot be null");
    }

    public static ResourceReservation of(ReservationId id, ExecutionIdentity executionId, ResourceType type, double amount, Instant expiresAt) {
        return new ResourceReservation(id, executionId, type, amount, expiresAt, ReservationState.RESERVED);
    }

    public ResourceReservation allocated() {
        return new ResourceReservation(id, executionId, type, amount, expiresAt, ReservationState.ALLOCATED);
    }

    public ResourceReservation released() {
        return new ResourceReservation(id, executionId, type, amount, expiresAt, ReservationState.RELEASED);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
