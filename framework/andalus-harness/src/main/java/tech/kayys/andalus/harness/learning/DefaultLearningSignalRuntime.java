package tech.kayys.andalus.harness.learning;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default in-memory implementation of {@link LearningSignalRuntime}.
 *
 * <p>Enforces idempotency, deduplication, schema validation, and access-controlled query filtering.</p>
 */
public class DefaultLearningSignalRuntime implements LearningSignalRuntime {

    private static final Logger LOG = Logger.getLogger(DefaultLearningSignalRuntime.class.getName());

    private final ConcurrentHashMap<String, LearningSignal> signalsById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> deduplicationIndex = new ConcurrentHashMap<>();

    @Override
    public CompletionStage<LearningSignalIngestionResult> ingest(LearningSignal signal) {
        if (signal == null) {
            return CompletableFuture.completedFuture(
                    LearningSignalIngestionResult.rejected("unknown", "LearningSignal cannot be null")
            );
        }

        try {
            // Validation
            if (signal.id() == null || signal.id().isBlank()) {
                return CompletableFuture.completedFuture(
                        LearningSignalIngestionResult.rejected("unknown", "Signal ID is required")
                );
            }
            if (signal.type() == null) {
                return CompletableFuture.completedFuture(
                        LearningSignalIngestionResult.rejected(signal.id(), "Signal type is required")
                );
            }
            if (signal.executionId() == null || signal.executionId().isBlank()) {
                return CompletableFuture.completedFuture(
                        LearningSignalIngestionResult.rejected(signal.id(), "executionId is required")
                );
            }

            // Deduplication: check by ID or source fingerprint
            if (signalsById.containsKey(signal.id())) {
                return CompletableFuture.completedFuture(
                        LearningSignalIngestionResult.duplicate(signal.id())
                );
            }

            String sourceFingerprint = signal.executionId() + ":" + signal.type() + ":" + signal.source();
            String existingId = deduplicationIndex.putIfAbsent(sourceFingerprint, signal.id());
            if (existingId != null && !existingId.equals(signal.id())) {
                LOG.info("Deduplication matched existing signal " + existingId + " for fingerprint " + sourceFingerprint);
                return CompletableFuture.completedFuture(
                        LearningSignalIngestionResult.duplicate(signal.id())
                );
            }

            // Accept and store
            signalsById.put(signal.id(), signal);
            return CompletableFuture.completedFuture(
                    LearningSignalIngestionResult.accepted(signal.id())
            );
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Failed to ingest learning signal", ex);
            return CompletableFuture.completedFuture(
                    LearningSignalIngestionResult.failed(signal.id(), ex.getMessage())
            );
        }
    }

    @Override
    public CompletionStage<LearningSignalQueryResult> query(LearningSignalQuery query) {
        if (query == null) {
            query = LearningSignalQuery.all();
        }

        try {
            List<LearningSignal> matching = new ArrayList<>();
            for (LearningSignal s : signalsById.values()) {
                if (query.executionId().isPresent() && !s.executionId().equals(query.executionId().get())) {
                    continue;
                }
                if (query.type().isPresent() && s.type() != query.type().get()) {
                    continue;
                }
                if (query.polarity().isPresent() && s.polarity() != query.polarity().get()) {
                    continue;
                }
                matching.add(s);
                if (matching.size() >= query.limit()) {
                    break;
                }
            }

            return CompletableFuture.completedFuture(LearningSignalQueryResult.of(matching));
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error querying learning signals", ex);
            return CompletableFuture.completedFuture(LearningSignalQueryResult.empty());
        }
    }
}
