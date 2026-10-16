package tech.kayys.andalus.harness.reasoning;

import java.util.concurrent.CompletionStage;

/**
 * SPI for reasoning providers that operate on a {@link ReasoningRequest}.
 *
 * <p>A {@code ContextAwareReasoner} is the harness-level equivalent of the
 * engine-level {@code Reasoner}. It accepts a full {@link ReasoningRequest}
 * (including the {@link tech.kayys.andalus.harness.protocol.DecisionContext})
 * and returns a {@link ReasoningAssessment}.</p>
 *
 * <h3>Contract</h3>
 * <ul>
 *   <li>{@link #id()} must be stable and unique among registered providers.</li>
 *   <li>{@link #reason(ReasoningRequest)} must not mutate the request or the
 *       context; reasoning is a read-only operation on the decision context.</li>
 *   <li>Ordinary failures (insufficient context, unsupported purpose, etc.)
 *       must be expressed as {@link ReasoningAssessment#failed(ReasoningPurpose, String)},
 *       not as exceptional completions, unless a truly unexpected error occurs.</li>
 *   <li>{@link #supports(ReasoningPurpose)} allows the runtime to select the
 *       most appropriate provider before invoking it.</li>
 * </ul>
 */
public interface ContextAwareReasoner {

    /** Stable identifier for this provider (e.g. {@code "chain-of-thought"}). */
    String id();

    /**
     * Produces a reasoning assessment for the given request.
     *
     * @param request the reasoning request
     * @return a {@link CompletionStage} resolving to a {@link ReasoningAssessment}
     */
    CompletionStage<ReasoningAssessment> reason(ReasoningRequest request);

    /**
     * Returns true if this provider can handle the given reasoning purpose.
     * Defaults to supporting all purposes; override for purpose-specific providers.
     */
    default boolean supports(ReasoningPurpose purpose) {
        return true;
    }
}
