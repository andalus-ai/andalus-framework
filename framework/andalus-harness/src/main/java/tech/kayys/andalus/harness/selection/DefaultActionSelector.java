package tech.kayys.andalus.harness.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of {@link ActionSelector}.
 *
 * <p>Coordinates candidate sources (tools and skills), applies {@link CandidateCompatibilityChecker},
 * and constructs a {@link SelectionResult} detailing included candidates and exclusions.</p>
 */
public class DefaultActionSelector implements ActionSelector {

    private static final Logger LOG = Logger.getLogger(DefaultActionSelector.class.getName());

    private final List<CandidateSource> sources;
    private final CandidateCompatibilityChecker compatibilityChecker;
    private final int maxCandidates;

    public DefaultActionSelector(
            List<CandidateSource> sources,
            CandidateCompatibilityChecker compatibilityChecker,
            int maxCandidates
    ) {
        this.sources = sources == null ? List.of() : List.copyOf(sources);
        this.compatibilityChecker = Objects.requireNonNull(compatibilityChecker, "compatibilityChecker is required");
        this.maxCandidates = maxCandidates > 0 ? maxCandidates : 20;
    }

    public DefaultActionSelector(List<CandidateSource> sources) {
        this(sources, new CandidateCompatibilityChecker(), 20);
    }

    public DefaultActionSelector(CandidateSource... sources) {
        this(sources != null ? List.of(sources) : List.of(), new CandidateCompatibilityChecker(), 20);
    }

    @Override
    public CompletionStage<SelectionResult> select(SelectionRequest request) {
        if (request == null) {
            return CompletableFuture.completedFuture(SelectionResult.failed("SelectionRequest cannot be null"));
        }

        try {
            List<CompletionStage<List<CandidateDescriptor>>> discoveryFutures = new ArrayList<>();
            for (CandidateSource source : sources) {
                if (isSourceEligible(source, request.target())) {
                    discoveryFutures.add(source.discover(request));
                }
            }

            if (discoveryFutures.isEmpty()) {
                return CompletableFuture.completedFuture(
                        SelectionResult.noCandidates(List.of(
                                CandidateExclusion.of("none", ExclusionReason.UNSUPPORTED_TARGET, "No sources available for target " + request.target())
                        ))
                );
            }

            return CompletableFuture.allOf(discoveryFutures.stream().map(CompletionStage::toCompletableFuture).toArray(CompletableFuture[]::new))
                    .thenApply(v -> {
                        List<CandidateDescriptor> allDiscovered = new ArrayList<>();
                        for (CompletionStage<List<CandidateDescriptor>> f : discoveryFutures) {
                            allDiscovered.addAll(f.toCompletableFuture().join());
                        }
                        return evaluateCandidates(allDiscovered, request);
                    })
                    .exceptionally(ex -> {
                        LOG.log(Level.WARNING, "Error during candidate selection", ex);
                        return SelectionResult.failed(ex.getMessage());
                    });
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Synchronous error during candidate selection", ex);
            return CompletableFuture.completedFuture(SelectionResult.failed(ex.getMessage()));
        }
    }

    private boolean isSourceEligible(CandidateSource source, SelectionTarget target) {
        if (target == SelectionTarget.TOOL_OR_SKILL) {
            return true;
        }
        return source.supportedTarget() == target;
    }

    private SelectionResult evaluateCandidates(List<CandidateDescriptor> descriptors, SelectionRequest request) {
        List<SelectionCandidate> candidates = new ArrayList<>();
        List<CandidateExclusion> exclusions = new ArrayList<>();

        for (CandidateDescriptor cd : descriptors) {
            Optional<CandidateExclusion> exclusion = compatibilityChecker.checkCompatibility(cd, request);
            if (exclusion.isPresent()) {
                exclusions.add(exclusion.get());
            } else {
                List<String> matched = new ArrayList<>(cd.capabilities());
                String rationale = "Candidate satisfies requirements for " + request.target();
                candidates.add(new SelectionCandidate(
                        cd.id(),
                        cd.target(),
                        matched,
                        List.of(),
                        rationale,
                        cd.metadata()
                ));
            }
        }

        if (candidates.isEmpty()) {
            if (request.requestedCapability().isPresent()) {
                String reqCap = request.requestedCapability().get();
                boolean anyHadCap = descriptors.stream().anyMatch(d -> d.capabilities().contains(reqCap));
                if (!anyHadCap) {
                    return new SelectionResult(
                            SelectionStatus.CAPABILITY_UNAVAILABLE,
                            List.of(),
                            exclusions,
                            java.util.Map.of("requestedCapability", reqCap)
                    );
                }
            }
            return SelectionResult.noCandidates(exclusions);
        }

        // Apply max candidates limit
        List<SelectionCandidate> limitedCandidates = candidates.size() > maxCandidates
                ? candidates.subList(0, maxCandidates)
                : candidates;

        return SelectionResult.found(limitedCandidates, exclusions);
    }
}
