package tech.kayys.wayang.execution.conformance;

/**
 * Classification of findings produced during contract conformance testing.
 */
public enum FindingType {
    CONTRACT_VIOLATION,
    MISSING_CAPABILITY,
    ENVIRONMENT_LIMITATION,
    IMPLEMENTATION_DEFECT,
    UNSUPPORTED_CONFIGURATION
}
