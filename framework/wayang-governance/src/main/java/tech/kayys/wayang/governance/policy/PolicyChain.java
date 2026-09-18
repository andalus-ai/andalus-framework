package tech.kayys.wayang.governance.policy;

import tech.kayys.wayang.governance.action.HarnessAction;

/**
 * Composable sequence of policies evaluated together.
 */
public interface PolicyChain {

    PolicyDecision evaluate(PolicyContext context, HarnessAction action);
}
