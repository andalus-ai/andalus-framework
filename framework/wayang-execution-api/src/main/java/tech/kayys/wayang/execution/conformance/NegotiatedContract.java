package tech.kayys.wayang.execution.conformance;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Formal contract resulting from negotiating requirements against runtime capabilities.
 */
public record NegotiatedContract(
    boolean agreed,
    Set<CapabilityDescriptor> agreedCapabilities,
    Set<Guarantee> agreedGuarantees,
    Set<CapabilityRequirement> unsatisfiedRequirements,
    Instant negotiatedAt
) {
    public NegotiatedContract {
        agreedCapabilities = agreedCapabilities != null ? Set.copyOf(agreedCapabilities) : Set.of();
        agreedGuarantees = agreedGuarantees != null ? Set.copyOf(agreedGuarantees) : Set.of();
        unsatisfiedRequirements = unsatisfiedRequirements != null ? Set.copyOf(unsatisfiedRequirements) : Set.of();
        Objects.requireNonNull(negotiatedAt, "negotiatedAt cannot be null");
    }

    public static NegotiatedContract success(Set<CapabilityDescriptor> capabilities, Set<Guarantee> guarantees) {
        return new NegotiatedContract(true, capabilities, guarantees, Set.of(), Instant.now());
    }

    public static NegotiatedContract rejected(Set<CapabilityRequirement> unsatisfied) {
        return new NegotiatedContract(false, Set.of(), Set.of(), unsatisfied, Instant.now());
    }
}
