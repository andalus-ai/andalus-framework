package tech.kayys.andalus.execution.core.conformance;

import tech.kayys.andalus.execution.conformance.CapabilityDescriptor;
import tech.kayys.andalus.execution.conformance.CapabilityRequirement;
import tech.kayys.andalus.execution.conformance.ConformanceProfile;
import tech.kayys.andalus.execution.conformance.ContractNegotiator;
import tech.kayys.andalus.execution.conformance.Guarantee;
import tech.kayys.andalus.execution.conformance.NegotiatedContract;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Negotiates capability requirements against an environment's declared profile.
 */
public class DefaultContractNegotiator implements ContractNegotiator {

    @Override
    public NegotiatedContract negotiate(Set<CapabilityRequirement> requirements, ConformanceProfile profile) {
        Objects.requireNonNull(profile, "profile cannot be null");
        if (requirements == null || requirements.isEmpty()) {
            return NegotiatedContract.success(profile.capabilities(), Set.of());
        }

        Map<tech.kayys.andalus.execution.conformance.CapabilityId, CapabilityDescriptor> available = profile.capabilities().stream()
            .collect(Collectors.toMap(CapabilityDescriptor::id, Function.identity(), (a, b) -> a));

        Set<CapabilityDescriptor> agreed = new HashSet<>();
        Set<Guarantee> guarantees = new HashSet<>();
        Set<CapabilityRequirement> unsatisfied = new HashSet<>();

        for (CapabilityRequirement req : requirements) {
            CapabilityDescriptor cap = available.get(req.requiredCapability());
            if (cap != null && req.satisfiedBy(cap)) {
                agreed.add(cap);
                guarantees.addAll(cap.guarantees());
            } else {
                unsatisfied.add(req);
            }
        }

        if (unsatisfied.isEmpty()) {
            return new NegotiatedContract(true, agreed, guarantees, Set.of(), Instant.now());
        } else {
            return NegotiatedContract.rejected(unsatisfied);
        }
    }
}
