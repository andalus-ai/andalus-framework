package tech.kayys.andalus.execution.policy;

import tech.kayys.andalus.execution.sandbox.SandboxContext;

/**
 * Unified Policy Decision Point (PDP) evaluating attempted operations against sandbox policies.
 */
public interface SandboxPolicyEngine {

    PolicyDecision evaluate(SandboxOperation operation, SandboxContext context);
}
