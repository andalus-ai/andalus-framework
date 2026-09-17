package tech.kayys.wayang.harness.governance.policy;

import tech.kayys.wayang.harness.governance.action.HarnessAction;

/**
 * Contract for policy evaluation governing whether and how actions may execute.
 */
public interface HarnessPolicy {

    PolicyDecision evaluate(PolicyContext context, HarnessAction action);
}
