package tech.kayys.andalus.harness.inference.planning;

import tech.kayys.andalus.harness.inference.InferencePlan;
import tech.kayys.andalus.harness.inference.InferenceRequirement;
import tech.kayys.andalus.harness.inference.OutputFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link InferencePlanner}.
 *
 * <p>Enforces two-stage planning:
 * <ol>
 *   <li><b>Stage A (Feasibility):</b> Filters candidates against mandatory requirements
 *       (capabilities, structured output, context limits, deployment mode, latency constraints).</li>
 *   <li><b>Stage B (Optimization):</b> For DIRECT, honors the requested model/provider;
 *       for ADAPTIVE, ranks feasible candidates by latency, cost, and stable tie-breaking.</li>
 * </ol>
 * </p>
 */
public class DefaultInferencePlanner implements InferencePlanner {

    private final ConcurrentHashMap<String, InferenceCandidate> candidates = new ConcurrentHashMap<>();
    private final PlanningStrategy defaultStrategy;
    private final DeploymentMode deploymentMode;

    public DefaultInferencePlanner(
            Collection<InferenceCandidate> initialCandidates,
            PlanningStrategy defaultStrategy,
            DeploymentMode deploymentMode
    ) {
        this.defaultStrategy = defaultStrategy != null ? defaultStrategy : PlanningStrategy.DIRECT;
        this.deploymentMode = deploymentMode != null ? deploymentMode : DeploymentMode.HYBRID;
        if (initialCandidates != null) {
            for (InferenceCandidate c : initialCandidates) {
                registerCandidate(c);
            }
        }
    }

    public DefaultInferencePlanner(PlanningStrategy strategy) {
        this(List.of(), strategy, DeploymentMode.HYBRID);
    }

    public DefaultInferencePlanner() {
        this(List.of(), PlanningStrategy.DIRECT, DeploymentMode.HYBRID);
    }

    public void registerCandidate(InferenceCandidate candidate) {
        if (candidate != null) {
            candidates.put(candidate.candidateId(), candidate);
        }
    }

    @Override
    public CompletionStage<InferencePlanningResult> plan(InferenceRequirement requirement) {
        if (requirement == null) {
            return CompletableFuture.completedFuture(
                    InferencePlanningResult.failed(
                            InferencePlanningStatus.FAILED,
                            "InferenceRequirement cannot be null",
                            List.of()
                    )
            );
        }

        List<InferenceCandidate> allCandidates = new ArrayList<>(candidates.values());
        List<InferenceCandidate> feasibleCandidates = new ArrayList<>();
        List<InferenceCandidateExclusion> exclusions = new ArrayList<>();

        // Stage A: Hard Feasibility Evaluation
        for (InferenceCandidate candidate : allCandidates) {
            Optional<InferenceCandidateExclusion> exclusion = evaluateFeasibility(candidate, requirement);
            if (exclusion.isPresent()) {
                exclusions.add(exclusion.get());
            } else {
                feasibleCandidates.add(candidate);
            }
        }

        PlanningStrategy strategy = resolveStrategy(requirement);

        if (strategy == PlanningStrategy.DIRECT) {
            return CompletableFuture.completedFuture(planDirect(requirement, feasibleCandidates, allCandidates, exclusions));
        } else {
            return CompletableFuture.completedFuture(planAdaptive(requirement, feasibleCandidates, exclusions));
        }
    }

    private Optional<InferenceCandidateExclusion> evaluateFeasibility(
            InferenceCandidate candidate,
            InferenceRequirement requirement
    ) {
        // 1. Deployment mode check
        if (deploymentMode == DeploymentMode.LOCAL_ONLY && !candidate.local()) {
            return Optional.of(InferenceCandidateExclusion.of(
                    candidate.candidateId(),
                    "DEPLOYMENT_RESTRICTION",
                    "Deployment mode is LOCAL_ONLY, but candidate is remote"
            ));
        }
        if (deploymentMode == DeploymentMode.CLOUD_ONLY && candidate.local()) {
            return Optional.of(InferenceCandidateExclusion.of(
                    candidate.candidateId(),
                    "DEPLOYMENT_RESTRICTION",
                    "Deployment mode is CLOUD_ONLY, but candidate is local"
            ));
        }

        // 2. Capability matching check
        for (String reqCap : requirement.capabilities()) {
            if (!candidate.capabilities().contains(reqCap) && !candidate.capabilities().contains("*")) {
                return Optional.of(InferenceCandidateExclusion.of(
                        candidate.candidateId(),
                        "CAPABILITY_MISMATCH",
                        "Candidate does not support required capability: " + reqCap
                ));
            }
        }

        // 3. Structured output format check
        OutputFormat format = requirement.output().format();
        if ((format == OutputFormat.JSON || format == OutputFormat.STRUCTURED)
                && !candidate.limits().supportsStructuredOutput()) {
            return Optional.of(InferenceCandidateExclusion.of(
                    candidate.candidateId(),
                    "UNSUPPORTED_OUTPUT_FORMAT",
                    "Candidate does not support structured/JSON output format"
            ));
        }

        // 4. Context token limit check
        if (requirement.input().estimatedTokens().isPresent()) {
            int estimated = requirement.input().estimatedTokens().get();
            if (estimated > candidate.limits().maxContextTokens()) {
                return Optional.of(InferenceCandidateExclusion.of(
                        candidate.candidateId(),
                        "CONTEXT_LIMIT_EXCEEDED",
                        "Estimated input tokens (" + estimated + ") exceed candidate max context (" + candidate.limits().maxContextTokens() + ")"
                ));
            }
        }

        // 5. Latency constraint check
        long maxLatency = requirement.constraints().maxLatencyMillis();
        if (maxLatency > 0 && candidate.telemetry().observedLatencyMillis().isPresent()) {
            long observed = candidate.telemetry().observedLatencyMillis().get();
            if (observed > maxLatency) {
                return Optional.of(InferenceCandidateExclusion.of(
                        candidate.candidateId(),
                        "LATENCY_BUDGET_EXCEEDED",
                        "Observed latency (" + observed + "ms) exceeds maximum latency (" + maxLatency + "ms)"
                ));
            }
        }

        return Optional.empty();
    }

    private InferencePlanningResult planDirect(
            InferenceRequirement requirement,
            List<InferenceCandidate> feasible,
            List<InferenceCandidate> all,
            List<InferenceCandidateExclusion> exclusions
    ) {
        if (requirement.requestedModel().isPresent()) {
            String requestedModel = requirement.requestedModel().get();

            Optional<InferenceCandidate> match = feasible.stream()
                    .filter(c -> c.modelId().equals(requestedModel))
                    .findFirst();

            if (match.isPresent()) {
                InferenceCandidate chosen = match.get();
                InferencePlan plan = createPlan(chosen, requirement, "DIRECT", "Requested model honored directly");
                return InferencePlanningResult.planned(plan, exclusions);
            }

            boolean existedAtAll = all.stream().anyMatch(c -> c.modelId().equals(requestedModel));
            if (existedAtAll) {
                return InferencePlanningResult.failed(
                        InferencePlanningStatus.CONSTRAINT_CONFLICT,
                        "Requested model '" + requestedModel + "' is infeasible under constraints",
                        exclusions
                );
            } else {
                return InferencePlanningResult.failed(
                        InferencePlanningStatus.MODEL_UNAVAILABLE,
                        "Requested model '" + requestedModel + "' is not registered",
                        exclusions
                );
            }
        }

        if (feasible.isEmpty()) {
            return buildNoFeasibleResult(requirement, exclusions);
        }

        InferenceCandidate first = feasible.get(0);
        InferencePlan plan = createPlan(first, requirement, "DIRECT", "Selected first feasible candidate under DIRECT strategy");
        return InferencePlanningResult.planned(plan, exclusions);
    }

    private InferencePlanningResult planAdaptive(
            InferenceRequirement requirement,
            List<InferenceCandidate> feasible,
            List<InferenceCandidateExclusion> exclusions
    ) {
        if (feasible.isEmpty()) {
            return buildNoFeasibleResult(requirement, exclusions);
        }

        // Stage B: Multi-criteria optimization ranking (latency ASC -> cost ASC -> stable candidateId ASC)
        feasible.sort(Comparator
                .comparingLong((InferenceCandidate c) -> c.telemetry().observedLatencyMillis().orElse(Long.MAX_VALUE))
                .thenComparingLong(c -> c.telemetry().estimatedCostPer1kTokensMicros().orElse(Long.MAX_VALUE))
                .thenComparing(InferenceCandidate::candidateId)
        );

        InferenceCandidate best = feasible.get(0);
        String rationale = "Selected optimal candidate by multi-criteria ranking (latency/cost/stability)";
        InferencePlan plan = createPlan(best, requirement, "ADAPTIVE", rationale);
        return InferencePlanningResult.planned(plan, exclusions);
    }

    private InferencePlan createPlan(
            InferenceCandidate candidate,
            InferenceRequirement requirement,
            String strategy,
            String rationale
    ) {
        return new InferencePlan(
                UUID.randomUUID().toString(),
                "",
                candidate.modelId(),
                Optional.of(candidate.providerId()),
                candidate.capabilities(),
                requirement.constraints(),
                strategy,
                Map.of(
                        "candidateId", candidate.candidateId(),
                        "local", candidate.local(),
                        "selectionRationale", rationale
                )
        );
    }

    private InferencePlanningResult buildNoFeasibleResult(
            InferenceRequirement requirement,
            List<InferenceCandidateExclusion> exclusions
    ) {
        boolean capabilityIssue = exclusions.stream().anyMatch(e -> "CAPABILITY_MISMATCH".equals(e.reason()));
        if (capabilityIssue && !requirement.capabilities().isEmpty()) {
            return InferencePlanningResult.failed(
                    InferencePlanningStatus.CAPABILITY_UNAVAILABLE,
                    "No candidate possesses all required capabilities: " + requirement.capabilities(),
                    exclusions
            );
        }
        return InferencePlanningResult.noFeasibleCandidate(exclusions);
    }

    private PlanningStrategy resolveStrategy(InferenceRequirement requirement) {
        Object strategyAttr = requirement.attributes().get("strategy");
        if (strategyAttr instanceof String str) {
            try {
                return PlanningStrategy.valueOf(str.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return defaultStrategy;
    }
}
