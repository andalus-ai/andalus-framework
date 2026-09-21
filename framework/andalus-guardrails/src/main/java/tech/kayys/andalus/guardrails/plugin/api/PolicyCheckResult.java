package tech.kayys.andalus.guardrails.plugin.api;

/**
 * Result of a policy check.
 */
public record PolicyCheckResult(
        String policyId,
        String policyName,
        boolean allowed,
        String denyMessage) {
}
