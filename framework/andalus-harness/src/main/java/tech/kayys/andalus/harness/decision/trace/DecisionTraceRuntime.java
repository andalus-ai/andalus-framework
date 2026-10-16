package tech.kayys.andalus.harness.decision.trace;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Runtime SPI for managing decision trace lifecycles.
 */
public interface DecisionTraceRuntime {

    /**
     * Starts a new decision trace.
     *
     * @param request the trace creation request
     * @return completion stage yielding the newly created decision trace
     */
    CompletionStage<DecisionTrace> start(DecisionTraceRequest request);

    /**
     * Appends an entry to an active decision trace.
     *
     * @param request the append request
     * @return completion stage yielding the updated decision trace
     */
    CompletionStage<DecisionTrace> append(DecisionTraceAppendRequest request);

    /**
     * Completes an active decision trace.
     *
     * @param request the completion request
     * @return completion stage yielding the completed decision trace
     */
    CompletionStage<DecisionTrace> complete(DecisionTraceCompletionRequest request);

    /**
     * Retrieves a decision trace matching the query.
     *
     * @param query the lookup query
     * @return completion stage yielding the optional decision trace
     */
    CompletionStage<Optional<DecisionTrace>> get(DecisionTraceQuery query);

    /**
     * Queries all decision traces matching the query criteria.
     *
     * @param query the search query
     * @return completion stage yielding matching traces
     */
    CompletionStage<List<DecisionTrace>> queryTraces(DecisionTraceQuery query);
}
