package tech.kayys.andalus.harness.learning;

import java.util.List;
import java.util.Optional;

/** Atomic reservation boundary for evidence consumed by admitted human feedback. */
public interface FeedbackEvidenceStore {
    enum ReservationStatus {
        RESERVED, ALREADY_RESERVED_BY_SAME_SIGNAL, EVIDENCE_ALREADY_CONSUMED, INVALID_REQUEST
/**
 * Atomic reservation boundary for evidence consumed by admitted human feedback.
 *
 * <p>Production implementations must persist reservations durably and enforce
 * uniqueness of each evidence reference across all writers (for example with
 * a database unique constraint). The complete reference batch must be committed
 * atomically: partial reservation is forbidden. A retry using the same signal
 * ID and identical ordered references is idempotent. Reusing a signal ID with
 * a different batch is rejected.</p>
 *
 * <p>This SPI deliberately has no database or transaction-framework dependency.
 * An adapter must document its durability, isolation, and recovery guarantees.</p>
 */
public interface FeedbackEvidenceStore {

    enum ReservationStatus {
        RESERVED,
        ALREADY_RESERVED_BY_SAME_SIGNAL,
        EVIDENCE_ALREADY_CONSUMED,
        INVALID_REQUEST
    }

    record Reservation(String signalId, List<String> evidenceReferences, ReservationStatus status) {
        public Reservation {
            if (signalId == null || signalId.isBlank() || status == null) {
                throw new IllegalArgumentException("signalId and status are required");
            }
            evidenceReferences = evidenceReferences == null ? List.of() : List.copyOf(evidenceReferences);
        }

        public boolean accepted() {
            return status == ReservationStatus.RESERVED
                    || status == ReservationStatus.ALREADY_RESERVED_BY_SAME_SIGNAL;
        }
    }

    /** Atomically reserves all references or none; exact retries for the same signal are idempotent. */
    Reservation reserve(String signalId, List<String> evidenceReferences);

    Optional<String> findOwner(String evidenceReference);
}
