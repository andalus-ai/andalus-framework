package tech.kayys.wayang.execution.policy;

import tech.kayys.wayang.execution.sandbox.SandboxContext;

/**
 * Unified Policy Decision Point (PDP) evaluating attempted operations against sandbox policies.
 */
public interface SandboxPolicyEngine {

    PolicyDecision evaluate(SandboxOperation operation, SandboxContext context);
}
