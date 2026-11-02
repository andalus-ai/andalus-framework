package tech.kayys.andalus.harness.learning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Process-local evidence reservation store for tests and development only.
 *
 * <p>This implementation is not durable, is not shared between processes,
 * and loses reservations on restart. Production deployments must provide a
 * transactional persistent implementation of {@link FeedbackEvidenceStore}.</p>
 */
public final class InMemoryFeedbackEvidenceStore implements FeedbackEvidenceStore {

    private final Map<String, String> ownerByEvidence = new HashMap<>();
    private final Map<String, List<String>> referencesBySignal = new HashMap<>();

    @Override
    public synchronized Reservation reserve(String signalId, List<String> evidenceReferences) {
        if (signalId == null || signalId.isBlank() || evidenceReferences == null
                || evidenceReferences.isEmpty()
                || evidenceReferences.stream().anyMatch(ref -> ref == null || ref.isBlank())
                || evidenceReferences.stream().distinct().count() != evidenceReferences.size()) {
            return new Reservation(signalId, evidenceReferences, ReservationStatus.INVALID_REQUEST);
        }

        List<String> requested = List.copyOf(evidenceReferences);
        List<String> existingForSignal = referencesBySignal.get(signalId);
        if (existingForSignal != null) {
            return new Reservation(signalId, requested,
                    existingForSignal.equals(requested)
                            ? ReservationStatus.ALREADY_RESERVED_BY_SAME_SIGNAL
                            : ReservationStatus.EVIDENCE_ALREADY_CONSUMED);
        }

        boolean conflict = requested.stream()
                .anyMatch(reference -> ownerByEvidence.containsKey(reference));
        if (conflict) {
            return new Reservation(signalId, requested, ReservationStatus.EVIDENCE_ALREADY_CONSUMED);
        }

        // All checks complete before mutating either index: no partial reservation.
        requested.forEach(reference -> ownerByEvidence.put(reference, signalId));
        referencesBySignal.put(signalId, requested);
        return new Reservation(signalId, requested, ReservationStatus.RESERVED);
    }

    @Override
    public synchronized Optional<String> findOwner(String evidenceReference) {
        return Optional.ofNullable(ownerByEvidence.get(evidenceReference));
    }
}
