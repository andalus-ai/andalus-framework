package tech.kayys.wayang.harness.consistency.state;

/**
 * Structured taxonomy of execution failure classes.
 */
public enum FailureClass {
    TRANSIENT,
    RESOURCE_EXHAUSTION,
    POLICY_DENIED,
    AUTHENTICATION,
    AUTHORIZATION,
    CONTRACT_INCOMPATIBLE,
    INVALID_INPUT,
    TOOL_FAILURE,
    PROVIDER_FAILURE,
    AGENT_FAILURE,
    DATA_CORRUPTION,
    UNKNOWN
}
