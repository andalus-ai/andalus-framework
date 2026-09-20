package tech.kayys.wayang.execution.economics;

import tech.kayys.wayang.execution.mobility.ExecutionIdentity;
import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.time.Instant;
import java.util.Objects;

/**
 * Concrete allocation converting a reservation into active execution resource quota.
 */
public record ResourceAllocation(
    ReservationId reservationId,
    ExecutionIdentity executionId,
    ResourceType type,
    double allocatedAmount,
    Instant allocatedAt
) {
    public ResourceAllocation {
        Objects.requireNonNull(reservationId, "reservationId cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(allocatedAt, "allocatedAt cannot be null");
    }

    public static ResourceAllocation of(ReservationId resId, ExecutionIdentity execId, ResourceType type, double amount) {
        return new ResourceAllocation(resId, execId, type, amount, Instant.now());
    }
}
