package tech.kayys.andalus.execution.economics;

import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.resource.model.ResourceType;

import java.time.Duration;
import java.util.Optional;

/**
 * Broker governing commitment, reservation, allocation, and release of resources.
 */
public interface ResourceBroker {

    ResourceReservation reserve(ExecutionIdentity executionId, ResourceType type, double amount, Duration ttl);

    Optional<ResourceAllocation> allocate(ReservationId reservationId);

    void release(ReservationId reservationId);
}
