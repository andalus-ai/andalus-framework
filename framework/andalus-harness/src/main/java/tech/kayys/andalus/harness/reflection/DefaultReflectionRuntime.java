package tech.kayys.andalus.harness.reflection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of {@link ReflectionRuntime}.
 *
 * <p>Analyzes outcomes based on {@link ReflectionTrigger}, produces structured {@link ReflectionFinding}s
 * and {@link ReflectionProposal}s, prevents recursive reflection loops, and bounds execution.</p>
 */
public class DefaultReflectionRuntime implements ReflectionRuntime {

    private static final Logger LOG = Logger.getLogger(DefaultReflectionRuntime.class.getName());

    private final int maxReflectionsPerExecution;
    private final int maxObservations;
    private final ConcurrentHashMap<String, AtomicInteger> reflectionCounts = new ConcurrentHashMap<>();

    public DefaultReflectionRuntime(int maxReflectionsPerExecution, int maxObservations) {
        this.maxReflectionsPerExecution = maxReflectionsPerExecution > 0 ? maxReflectionsPerExecution : 5;
        this.maxObservations = maxObservations > 0 ? maxObservations : 50;
    }

    public DefaultReflectionRuntime() {
        this(5, 50);
    }

    @Override
    public CompletionStage<ReflectionResult> reflect(ReflectionRequest request) {
        if (request == null) {
            return CompletableFuture.completedFuture(
                    ReflectionResult.failed("ReflectionRequest cannot be null")
            );
        }

        try {
            // Guard against recursive/excessive reflection loops
            AtomicInteger count = reflectionCounts.computeIfAbsent(request.executionId(), k -> new AtomicInteger(0));
            if (count.incrementAndGet() > maxReflectionsPerExecution) {
                LOG.warning("Max reflections reached (" + maxReflectionsPerExecution + ") for execution: " + request.executionId());
                return CompletableFuture.completedFuture(
                        new ReflectionResult(
                                java.util.UUID.randomUUID().toString(),
                                ReflectionStatus.SKIPPED,
                                List.of(),
                                List.of(),
                                List.of(),
                                java.time.Instant.now(),
                                Map.of("reason", "Max reflections per execution limit exceeded")
                        )
                );
            }

            List<String> obsRefs = request.observationReferences();
            if (obsRefs.size() > maxObservations) {
                obsRefs = obsRefs.subList(0, maxObservations);
            }

            return CompletableFuture.completedFuture(evaluateTrigger(request, obsRefs));
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error during reflection", ex);
            return CompletableFuture.completedFuture(ReflectionResult.failed(ex.getMessage()));
        }
    }

    private ReflectionResult evaluateTrigger(ReflectionRequest request, List<String> obsRefs) {
        ReflectionTrigger trigger = request.trigger();
        List<ReflectionFinding> findings = new ArrayList<>();
        List<ReflectionProposal> proposals = new ArrayList<>();

        switch (trigger) {
            case ACTION_FAILURE -> {
                if (obsRefs.isEmpty()) {
                    return ReflectionResult.insufficientEvidence("No failure observations provided for ACTION_FAILURE reflection");
                }
                ReflectionFinding failureFinding = ReflectionFinding.of(
                        ReflectionFindingType.TOOL_FAILURE_PATTERN,
                        "Action execution failed; pattern indicates unmet prerequisite or faulty argument",
                        obsRefs
                );
                findings.add(failureFinding);

                ReflectionProposal retryProposal = ReflectionProposal.of(
                        ReflectionProposalType.RETRY_WITH_CHANGED_APPROACH,
                        "Inspect action preconditions or adjust parameter schema on subsequent attempt",
                        List.of(failureFinding.id())
                );
                proposals.add(retryProposal);
            }
            case TASK_COMPLETION -> {
                ReflectionFinding successFinding = ReflectionFinding.of(
                        ReflectionFindingType.OUTCOME_CONFIRMED,
                        "Task completion criteria satisfied based on observation evidence",
                        obsRefs
                );
                findings.add(successFinding);

                ReflectionProposal confirmProposal = ReflectionProposal.of(
                        ReflectionProposalType.NO_CHANGE,
                        "Task completed successfully; continue to next workflow step",
                        List.of(successFinding.id())
                );
                proposals.add(confirmProposal);
            }
            case CONTRADICTING_EVIDENCE -> {
                if (obsRefs.isEmpty()) {
                    return ReflectionResult.insufficientEvidence("No observation references provided for CONTRADICTING_EVIDENCE");
                }
                ReflectionFinding conflictFinding = ReflectionFinding.of(
                        ReflectionFindingType.CONTRADICTING_EVIDENCE,
                        "New observation evidence conflicts with active belief interpretation",
                        obsRefs
                );
                findings.add(conflictFinding);

                ReflectionProposal beliefProposal = ReflectionProposal.of(
                        ReflectionProposalType.REASSESS_BELIEF,
                        "Trigger belief reassessment to transition disputed interpretation",
                        List.of(conflictFinding.id())
                );
                proposals.add(beliefProposal);
            }
            case HUMAN_CORRECTION -> {
                ReflectionFinding correctionFinding = ReflectionFinding.of(
                        ReflectionFindingType.POTENTIAL_IMPROVEMENT,
                        "User feedback provided correction on action strategy or output formatting",
                        obsRefs
                );
                findings.add(correctionFinding);

                ReflectionProposal memoryProposal = ReflectionProposal.of(
                        ReflectionProposalType.PROMOTE_TO_MEMORY_CANDIDATE,
                        "Consider proposing user correction preference to MemoryRuntime admission",
                        List.of(correctionFinding.id())
                );
                proposals.add(memoryProposal);
            }
            case PLAN_DEVIATION -> {
                ReflectionFinding planFinding = ReflectionFinding.of(
                        ReflectionFindingType.PLAN_DEVIATION,
                        "Observed execution diverged from proposed plan steps",
                        obsRefs
                );
                findings.add(planFinding);

                ReflectionProposal replanProposal = ReflectionProposal.of(
                        ReflectionProposalType.REVISE_PLAN,
                        "Replan remaining steps to realign execution with the goal",
                        List.of(planFinding.id())
                );
                proposals.add(replanProposal);
            }
            default -> {
                ReflectionFinding generalFinding = ReflectionFinding.of(
                        ReflectionFindingType.POTENTIAL_IMPROVEMENT,
                        "Execution review completed for trigger: " + trigger,
                        obsRefs
                );
                findings.add(generalFinding);

                ReflectionProposal noChange = ReflectionProposal.of(
                        ReflectionProposalType.NO_CHANGE,
                        "Standard lifecycle progression",
                        List.of(generalFinding.id())
                );
                proposals.add(noChange);
            }
        }

        return ReflectionResult.completed(findings, proposals, obsRefs);
    }
}
