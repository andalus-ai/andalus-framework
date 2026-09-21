package tech.kayys.andalus.execution.core.economics;

import tech.kayys.andalus.execution.economics.ReservationId;
import tech.kayys.andalus.execution.economics.ReservationState;
import tech.kayys.andalus.execution.economics.ResourceAllocation;
import tech.kayys.andalus.execution.economics.ResourceBroker;
import tech.kayys.andalus.execution.economics.ResourceLedger;
import tech.kayys.andalus.execution.economics.ResourceReservation;
import tech.kayys.andalus.execution.economics.ResourceUsage;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.resource.model.ResourceType;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default resource broker managing leases, reservations, allocations, and releases.
 */
public class DefaultResourceBroker implements ResourceBroker {

    private final ResourceLedger ledger;
    private final Map<ReservationId, ResourceReservation> reservations = new ConcurrentHashMap<>();

    public DefaultResourceBroker(ResourceLedger ledger) {
        this.ledger = Objects.requireNonNull(ledger, "ledger cannot be null");
    }

    public DefaultResourceBroker() {
        this(new DefaultResourceLedger());
    }

    @Override
    public ResourceReservation reserve(ExecutionIdentity executionId, ResourceType type, double amount, Duration ttl) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Duration duration = ttl != null ? ttl : Duration.ofMinutes(5);

        ReservationId resId = ReservationId.generate();
        Instant expiresAt = Instant.now().plus(duration);
        ResourceReservation reservation = ResourceReservation.of(resId, executionId, type, amount, expiresAt);
        reservations.put(resId, reservation);
        return reservation;
    }

    @Override
    public Optional<ResourceAllocation> allocate(ReservationId reservationId) {
        if (reservationId == null) return Optional.empty();

        ResourceReservation res = reservations.get(reservationId);
        if (res == null || res.state() != ReservationState.RESERVED || res.isExpired()) {
            return Optional.empty();
        }

        ResourceReservation updated = res.allocated();
        reservations.put(reservationId, updated);

        // Record allocation as active debit in ledger
        ledger.recordUsage(ResourceUsage.of(res.executionId(), res.type(), res.amount()));

        return Optional.of(ResourceAllocation.of(reservationId, res.executionId(), res.type(), res.amount()));
    }

    @Override
    public void release(ReservationId reservationId) {
        if (reservationId != null) {
            ResourceReservation res = reservations.get(reservationId);
            if (res != null) {
                reservations.put(reservationId, res.released());
            }
        }
    }
}
