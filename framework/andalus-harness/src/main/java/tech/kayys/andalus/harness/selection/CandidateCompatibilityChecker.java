package tech.kayys.andalus.harness.selection;

import java.util.Map;
import java.util.Optional;

/**
 * Evaluates whether a candidate tool or skill satisfies the hard constraints and requirements of a selection request.
 */
public class CandidateCompatibilityChecker {

    /**
     * Checks if a candidate is compatible with the given selection request.
     *
     * @param candidate the candidate descriptor
     * @param request the selection request
     * @return an {@link Optional} containing {@link CandidateExclusion} if incompatible, or empty if compatible
     */
    public Optional<CandidateExclusion> checkCompatibility(CandidateDescriptor candidate, SelectionRequest request) {
        if (candidate == null) {
            return Optional.of(CandidateExclusion.of("unknown", ExclusionReason.UNSUPPORTED_TARGET, "Candidate is null"));
        }

        // 1. Target check
        if (request.target() != SelectionTarget.TOOL_OR_SKILL && request.target() != candidate.target()) {
            return Optional.of(CandidateExclusion.of(
                    candidate.id(),
                    ExclusionReason.UNSUPPORTED_TARGET,
                    "Candidate target " + candidate.target() + " does not match requested target " + request.target()
            ));
        }

        // 2. Disabled check
        if (!candidate.enabled()) {
            return Optional.of(CandidateExclusion.of(
                    candidate.id(),
                    ExclusionReason.DISABLED,
                    "Candidate '" + candidate.id() + "' is disabled"
            ));
        }

        // 3. Capability requirement check
        if (request.requestedCapability().isPresent()) {
            String requiredCap = request.requestedCapability().get();
            if (!candidate.capabilities().contains(requiredCap) && !candidate.capabilities().contains("*")) {
                return Optional.of(CandidateExclusion.of(
                        candidate.id(),
                        ExclusionReason.CAPABILITY_MISMATCH,
                        "Candidate '" + candidate.id() + "' does not possess required capability '" + requiredCap + "'"
                ));
            }
        }

        // 4. Input requirements schema check
        Map<String, Object> inputReqs = request.inputRequirements();
        if (!inputReqs.isEmpty() && !candidate.inputSchema().isEmpty()) {
            for (String requiredKey : inputReqs.keySet()) {
                if (!candidate.inputSchema().containsKey(requiredKey)) {
                    return Optional.of(CandidateExclusion.of(
                            candidate.id(),
                            ExclusionReason.INPUT_SCHEMA_MISMATCH,
                            "Candidate input schema missing required input field: " + requiredKey
                    ));
                }
            }
        }

        return Optional.empty();
    }
}
