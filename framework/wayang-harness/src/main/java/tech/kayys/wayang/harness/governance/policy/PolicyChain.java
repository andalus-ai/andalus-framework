package tech.kayys.wayang.harness.governance.policy;

import tech.kayys.wayang.harness.governance.action.HarnessAction;

/**
 * Composable sequence of policies evaluated together.
 */
public interface PolicyChain {

    PolicyDecision evaluate(PolicyContext context, HarnessAction action);
}
