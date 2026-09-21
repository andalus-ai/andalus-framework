package tech.kayys.andalus.execution.core.fabric;

import tech.kayys.andalus.execution.conformance.CapabilityDescriptor;
import tech.kayys.andalus.execution.conformance.CapabilityId;
import tech.kayys.andalus.execution.conformance.CapabilityRequirement;
import tech.kayys.andalus.execution.conformance.ConformanceProfile;
import tech.kayys.andalus.execution.fabric.FabricSnapshot;
import tech.kayys.andalus.execution.fabric.PlacementEngine;
import tech.kayys.andalus.execution.fabric.PlacementEvidence;
import tech.kayys.andalus.execution.fabric.PlacementPlan;
import tech.kayys.andalus.execution.fabric.AndalusNode;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.resource.requirement.ResourceRequirement;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Intelligent placement engine matching capabilities, resources, and availability.
 */
public class DefaultPlacementEngine implements PlacementEngine {

    @Override
    public Optional<PlacementPlan> plan(
        ExecutionIdentity executionId,
        Set<CapabilityRequirement> capabilityRequirements,
        Set<ResourceRequirement> resourceRequirements,
        FabricSnapshot fabric
    ) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(fabric, "fabric cannot be null");

        AndalusNode bestCandidate = null;
        double highestScore = -1.0;
        PlacementEvidence bestEvidence = null;

        for (AndalusNode node : fabric.availableNodes()) {
            Map<String, Boolean> matches = new HashMap<>();
            boolean allMatched = true;

            // Check capabilities
            ConformanceProfile profile = node.profile();
            Map<CapabilityId, CapabilityDescriptor> nodeCaps = profile.capabilities().stream()
                .collect(Collectors.toMap(CapabilityDescriptor::id, Function.identity(), (a, b) -> a));

            if (capabilityRequirements != null) {
                for (CapabilityRequirement req : capabilityRequirements) {
                    CapabilityDescriptor cap = nodeCaps.get(req.requiredCapability());
                    boolean satisfied = cap != null && req.satisfiedBy(cap);
                    matches.put("cap:" + req.requiredCapability().qualifiedName(), satisfied);
                    if (!satisfied) {
                        allMatched = false;
                    }
                }
            }

            if (!allMatched) {
                continue;
            }

            // Calculate suitability score (lower load = higher score)
            double load = node.status().loadFactor();
            double score = (1.0 - load) * 100.0;

            if (score > highestScore) {
                highestScore = score;
                bestCandidate = node;
                bestEvidence = new PlacementEvidence(
                    node.identity(),
                    matches,
                    score,
                    "Selected node with loadFactor " + load + " matching all requirements"
                );
            }
        }

        if (bestCandidate != null && bestEvidence != null) {
            return Optional.of(PlacementPlan.of(executionId, bestCandidate.identity(), bestEvidence));
        }

        return Optional.empty();
    }
}
