package tech.kayys.wayang.execution.governance;

/**
 * Baseline fallback effect when no explicit policy rule matches an invocation.
 */
public enum PolicyDefaultEffect {

    ALLOW,

    DENY
}
