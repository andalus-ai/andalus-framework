package tech.kayys.wayang.execution.governance;

/**
 * The effect desired or produced by a governance policy rule or evaluation.
 */
public enum PolicyEffect {

    /**
     * The invocation may proceed.
     */
    ALLOW,

    /**
     * The invocation must not proceed.
     */
    DENY,

    /**
     * The invocation may proceed only after explicit approval is granted.
     */
    REQUIRE_APPROVAL
}
