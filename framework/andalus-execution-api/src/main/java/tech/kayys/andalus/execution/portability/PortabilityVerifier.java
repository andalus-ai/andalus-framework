package tech.kayys.andalus.execution.portability;

import tech.kayys.andalus.execution.conformance.ConformanceProfile;

/**
 * Verifier evaluating if a portable execution can be safely executed on a target profile.
 */
public interface PortabilityVerifier {

    CompatibilityReport verifyCompatibility(PortableExecution execution, ConformanceProfile targetProfile);
}
