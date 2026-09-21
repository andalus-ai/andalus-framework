package tech.kayys.andalus.execution.core.portability;

import tech.kayys.andalus.execution.conformance.CapabilityDescriptor;
import tech.kayys.andalus.execution.conformance.CapabilityId;
import tech.kayys.andalus.execution.conformance.CapabilityRequirement;
import tech.kayys.andalus.execution.conformance.ConformanceProfile;
import tech.kayys.andalus.execution.conformance.ContractId;
import tech.kayys.andalus.execution.portability.CompatibilityIssue;
import tech.kayys.andalus.execution.portability.CompatibilityReport;
import tech.kayys.andalus.execution.portability.ExecutionManifest;
import tech.kayys.andalus.execution.portability.ExecutionPackage;
import tech.kayys.andalus.execution.portability.PortableExecution;
import tech.kayys.andalus.execution.portability.PortabilityVerifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Evaluates whether a portable execution is compatible with a target runtime's profile.
 */
public class DefaultPortabilityVerifier implements PortabilityVerifier {

    @Override
    public CompatibilityReport verifyCompatibility(PortableExecution execution, ConformanceProfile targetProfile) {
        Objects.requireNonNull(execution, "execution cannot be null");
        Objects.requireNonNull(targetProfile, "targetProfile cannot be null");

        // Verify package integrity
        ExecutionPackage pkg = new ExecutionPackage(execution);
        if (!pkg.verifyIntegrity()) {
            return CompatibilityReport.failure(
                List.of(CompatibilityIssue.error("PACKAGE_CORRUPTED", "Digest mismatch for portable execution payload")),
                Set.of()
            );
        }

        ExecutionManifest manifest = execution.manifest();
        List<CompatibilityIssue> issues = new ArrayList<>();
        Set<CapabilityId> supported = new HashSet<>();
        Set<CapabilityId> missing = new HashSet<>();

        // Check required contracts
        for (ContractId requiredContract : manifest.contracts()) {
            if (!targetProfile.supportsContract(requiredContract)) {
                issues.add(CompatibilityIssue.error("UNSUPPORTED_CONTRACT", "Target profile missing contract: " + requiredContract.canonicalName()));
            }
        }

        // Check capability requirements
        Map<CapabilityId, CapabilityDescriptor> availableCaps = targetProfile.capabilities().stream()
            .collect(Collectors.toMap(CapabilityDescriptor::id, Function.identity(), (a, b) -> a));

        for (CapabilityRequirement req : manifest.capabilities()) {
            CapabilityDescriptor descriptor = availableCaps.get(req.requiredCapability());
            if (descriptor != null && req.satisfiedBy(descriptor)) {
                supported.add(descriptor.id());
            } else {
                missing.add(req.requiredCapability());
                issues.add(CompatibilityIssue.error("UNSATISFIED_CAPABILITY", "Requirement unsatisfied: " + req.requiredCapability().qualifiedName()));
            }
        }

        if (issues.isEmpty()) {
            return CompatibilityReport.success(supported);
        } else {
            return CompatibilityReport.failure(issues, missing);
        }
    }
}
