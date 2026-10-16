package tech.kayys.andalus.harness.reasoning;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default {@link ReasoningRuntime} implementation.
 *
 * <h3>Algorithm</h3>
 * <ol>
 *   <li>Resolve a {@link ContextAwareReasoner} from the {@link ReasonerRegistry}
 *       that supports the request's {@link ReasoningPurpose}. If none is found
 *       return a failed assessment immediately.</li>
 *   <li>Invoke {@link ContextAwareReasoner#reason(ReasoningRequest)}.</li>
 *   <li>Validate the returned {@link ReasoningAssessment} via
 *       {@link ReasoningResultValidator}. If invalid, wrap into a failed assessment.</li>
 *   <li>Catch any unexpected exception and return a controlled failed assessment;
 *       never propagate unexpectedly to the caller.</li>
 * </ol>
 *
 * <p>This implementation does not yet enforce time limits or budget constraints —
 * those are extension points to be added when the execution budget API (Phase 4G)
 * is integrated.</p>
 */
public class DefaultReasoningRuntime implements ReasoningRuntime {

    private static final Logger LOG = Logger.getLogger(DefaultReasoningRuntime.class.getName());

    private final ReasonerRegistry registry;
    private final ReasoningResultValidator validator;

    public DefaultReasoningRuntime(ReasonerRegistry registry, ReasoningResultValidator validator) {
        if (registry == null) throw new IllegalArgumentException("registry must not be null");
        if (validator == null) throw new IllegalArgumentException("validator must not be null");
        this.registry = registry;
        this.validator = validator;
    }

    /** Convenience constructor with default validator. */
    public DefaultReasoningRuntime(ReasonerRegistry registry) {
        this(registry, new ReasoningResultValidator());
    }

    // ── ReasoningRuntime ──────────────────────────────────────────────────────

    @Override
    public CompletionStage<ReasoningAssessment> reason(ReasoningRequest request) {
        ReasoningPurpose purpose = request.purpose();

        Optional<ContextAwareReasoner> reasonerOpt = registry.findFor(purpose);
        if (reasonerOpt.isEmpty()) {
            return CompletableFuture.completedFuture(
                    ReasoningAssessment.failed(purpose,
                            "No reasoning provider registered for purpose: " + purpose));
        }

        ContextAwareReasoner reasoner = reasonerOpt.get();

        try {
            return reasoner.reason(request)
                    .thenApply(assessment -> validateAssessment(assessment, purpose))
                    .exceptionally(ex -> {
                        LOG.log(Level.WARNING, "Reasoning provider threw unexpected exception", ex);
                        return ReasoningAssessment.failed(purpose,
                                "Reasoning provider error: " + ex.getMessage());
                    });
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Reasoning invocation failed synchronously", ex);
            return CompletableFuture.completedFuture(
                    ReasoningAssessment.failed(purpose,
                            "Reasoning invocation error: " + ex.getMessage()));
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private ReasoningAssessment validateAssessment(ReasoningAssessment assessment,
                                                   ReasoningPurpose purpose) {
        if (assessment == null) {
            return ReasoningAssessment.failed(purpose, "Reasoning provider returned null assessment.");
        }
        List<String> errors = validator.validate(assessment);
        if (!errors.isEmpty()) {
            String reason = "Invalid reasoning assessment: " + String.join("; ", errors);
            LOG.warning(reason);
            return ReasoningAssessment.failed(purpose, reason);
        }
        return assessment;
    }
}
