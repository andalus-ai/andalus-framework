package tech.kayys.andalus.harness.reasoning;

import java.util.concurrent.CompletionStage;

/**
 * Runtime contract for coordinating reasoning requests and their results.
 *
 * <p>The {@code ReasoningRuntime} is the single integration point between
 * the agent decision pipeline and reasoning providers. Its responsibilities
 * are:</p>
 * <ol>
 *   <li>Receive an immutable {@link ReasoningRequest}.</li>
 *   <li>Resolve the appropriate {@link ContextAwareReasoner}.</li>
 *   <li>Invoke the reasoner under configured time and resource limits.</li>
 *   <li>Validate the returned {@link ReasoningAssessment} structure.</li>
 *   <li>Return a concise, decision-relevant assessment.</li>
 *   <li>Preserve evidence references and provenance.</li>
 * </ol>
 *
 * <p>The runtime must <em>not</em> execute tools or skills, own the agent
 * loop, schedule workflows, mutate goal or task state, perform authorization,
 * replace the Planner, own model inference, or automatically persist results
 * as memory.</p>
 */
public interface ReasoningRuntime {

    /**
     * Performs a reasoning operation for the given request.
     *
     * @param request the reasoning request containing decision context, optional plan,
     *                purpose, and attributes
     * @return a {@link CompletionStage} resolving to a {@link ReasoningAssessment};
     *         failures are represented as assessments with {@code status=FAILED}, not as
     *         exceptional completions, unless a truly unexpected error occurs
     */
    CompletionStage<ReasoningAssessment> reason(ReasoningRequest request);
}
