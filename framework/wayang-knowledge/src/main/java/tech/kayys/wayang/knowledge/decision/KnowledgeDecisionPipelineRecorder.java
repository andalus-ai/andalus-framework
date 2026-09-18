package tech.kayys.wayang.knowledge.decision;

/**
 * Provides knowledge decision pipeline recorder behavior for the Wayang framework.
 */


public final class KnowledgeDecisionPipelineRecorder {

    private final KnowledgeDecisionTraceService traceService;

    public KnowledgeDecisionPipelineRecorder(KnowledgeDecisionTraceService traceService) {
        this.traceService = traceService;
    }

    public KnowledgeDecisionTrace finish(
            KnowledgeDecisionRecorder recorder,
            KnowledgeDecisionOutcome outcome) {

        KnowledgeDecisionTrace trace = recorder
                .outcome(outcome)
                .build();

        if (traceService != null) {
            traceService.record(trace);
        }

        return trace;
    }
}
