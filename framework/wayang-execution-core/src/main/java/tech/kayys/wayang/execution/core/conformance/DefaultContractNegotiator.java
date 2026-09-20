package tech.kayys.wayang.execution.core.conformance;

import tech.kayys.wayang.execution.conformance.CapabilityDescriptor;
import tech.kayys.wayang.execution.conformance.CapabilityRequirement;
import tech.kayys.wayang.execution.conformance.ConformanceProfile;
import tech.kayys.wayang.execution.conformance.ContractNegotiator;
import tech.kayys.wayang.execution.conformance.Guarantee;
import tech.kayys.wayang.execution.conformance.NegotiatedContract;

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

        Map<tech.kayys.wayang.execution.conformance.CapabilityId, CapabilityDescriptor> available = profile.capabilities().stream()
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
