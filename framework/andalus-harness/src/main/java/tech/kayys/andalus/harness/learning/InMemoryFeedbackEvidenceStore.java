package tech.kayys.andalus.harness.learning;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Process-local reference implementation. Durable deployments should provide a
 * transactional implementation backed by a shared database.
 */
public final class InMemoryFeedbackEvidenceStore implements FeedbackEvidenceStore {
    private final Map<String, String> ownersByReference = new ConcurrentHashMap<>();
    private final Map<String, List<String>> referencesBySignal = new ConcurrentHashMap<>();

    @Override
    public synchronized Reservation reserve(String signalId, List<String> evidenceReferences) {
        if (signalId == null || signalId.isBlank() || evidenceReferences == null || evidenceReferences.isEmpty()) {
            return new Reservation("invalid", List.of(), ReservationStatus.INVALID_REQUEST);
        }
        if (evidenceReferences.stream().anyMatch(reference -> reference == null || reference.isBlank())
                || new LinkedHashSet<>(evidenceReferences).size() != evidenceReferences.size()) {
            return new Reservation(signalId, List.of(), ReservationStatus.INVALID_REQUEST);
        }

        List<String> requested = List.copyOf(evidenceReferences);
        List<String> prior = referencesBySignal.get(signalId);
        if (prior != null) {
            return new Reservation(signalId, requested, prior.equals(requested)
                    ? ReservationStatus.ALREADY_RESERVED_BY_SAME_SIGNAL
                    : ReservationStatus.EVIDENCE_ALREADY_CONSUMED);
        }
        if (requested.stream().anyMatch(ownersByReference::containsKey)) {
            return new Reservation(signalId, requested, ReservationStatus.EVIDENCE_ALREADY_CONSUMED);
        }

        // Check and write occur under one monitor: no partial reservation on conflict.
        requested.forEach(reference -> ownersByReference.put(reference, signalId));
        referencesBySignal.put(signalId, requested);
        return new Reservation(signalId, requested, ReservationStatus.RESERVED);
    }

    @Override
    public Optional<String> findOwner(String evidenceReference) {
        if (evidenceReference == null || evidenceReference.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ownersByReference.get(evidenceReference));
    }
}
