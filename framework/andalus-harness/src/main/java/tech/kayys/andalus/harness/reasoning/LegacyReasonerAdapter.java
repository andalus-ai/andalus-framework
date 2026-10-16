package tech.kayys.andalus.harness.reasoning;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * Bridges a legacy function-style or synchronous reasoning provider to the
 * {@link ContextAwareReasoner} SPI.
 *
 * <p>This adapter allows existing code that produces a {@link ReasoningAssessment}
 * synchronously (e.g. deterministic validators, rule-based systems) to be
 * registered in the {@link ReasonerRegistry} without implementing the full
 * async interface.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * ContextAwareReasoner reasoner = new LegacyReasonerAdapter(
 *         "plan-validator",
 *         ReasoningPurpose.EVALUATE_PLAN,
 *         req -> ReasoningAssessment.of(req.purpose(), "Plan looks valid"));
 * registry.register(reasoner);
 * }</pre>
 */
public class LegacyReasonerAdapter implements ContextAwareReasoner {

    private final String id;
    private final ReasoningPurpose supportedPurpose;
    private final Function<ReasoningRequest, ReasoningAssessment> delegate;

    /**
     * Constructs an adapter for a synchronous reasoning function scoped to a single purpose.
     *
     * @param id              stable provider identifier
     * @param supportedPurpose the {@link ReasoningPurpose} this adapter handles
     * @param delegate        synchronous function that produces an assessment
     */
    public LegacyReasonerAdapter(String id,
                                  ReasoningPurpose supportedPurpose,
                                  Function<ReasoningRequest, ReasoningAssessment> delegate) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id must not be blank");
        if (delegate == null) throw new IllegalArgumentException("delegate must not be null");
        this.id = id;
        this.supportedPurpose = supportedPurpose;
        this.delegate = delegate;
    }

    /** Constructs an adapter that supports all reasoning purposes. */
    public LegacyReasonerAdapter(String id,
                                  Function<ReasoningRequest, ReasoningAssessment> delegate) {
        this(id, null, delegate);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean supports(ReasoningPurpose purpose) {
        return supportedPurpose == null || supportedPurpose == purpose;
    }

    @Override
    public CompletionStage<ReasoningAssessment> reason(ReasoningRequest request) {
        try {
            ReasoningAssessment result = delegate.apply(request);
            return CompletableFuture.completedFuture(result);
        } catch (Exception ex) {
            return CompletableFuture.completedFuture(
                    ReasoningAssessment.failed(request.purpose(),
                            "LegacyReasonerAdapter error: " + ex.getMessage()));
        }
    }
}
