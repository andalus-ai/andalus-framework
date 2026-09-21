package tech.kayys.andalus.execution.economics;

import java.util.Objects;
import java.util.Optional;

/**
 * Gatekeeping decision determining whether an execution is admitted to the runtime fabric.
 */
public record AdmissionDecision(
    boolean admitted,
    String reason,
    Optional<ReservationId> reservationId
) {
    public AdmissionDecision {
        Objects.requireNonNull(reason, "reason cannot be null");
        Objects.requireNonNull(reservationId, "reservationId cannot be null");
    }

    public static AdmissionDecision admit(ReservationId resId) {
        return new AdmissionDecision(true, "Admitted successfully", Optional.of(resId));
    }

    public static AdmissionDecision reject(String reason) {
        return new AdmissionDecision(false, reason, Optional.empty());
    }
}
