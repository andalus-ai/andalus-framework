package tech.kayys.andalus.harness.learning;

import java.util.List;
import java.util.Optional;

/** Atomic reservation boundary for evidence consumed by admitted human feedback. */
public interface FeedbackEvidenceStore {
    enum ReservationStatus {
        RESERVED, ALREADY_RESERVED_BY_SAME_SIGNAL, EVIDENCE_ALREADY_CONSUMED, INVALID_REQUEST
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