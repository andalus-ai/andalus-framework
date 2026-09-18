package tech.kayys.wayang.governance.policy;

import tech.kayys.wayang.governance.action.HarnessAction;

/**
 * Contract for policy evaluation governing whether and how actions may execute.
 */
public interface HarnessPolicy {

    PolicyDecision evaluate(PolicyContext context, HarnessAction action);
}
